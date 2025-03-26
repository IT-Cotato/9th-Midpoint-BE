package middle_point_search.backend.domains.place.service;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.domains.google.service.GoogleService;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.memberRoom.service.MemberRoomValidateService;
import middle_point_search.backend.domains.place.domain.Place;
import middle_point_search.backend.domains.place.dto.request.SavePlaceRequest;
import middle_point_search.backend.domains.place.dto.request.UpdatePlaceRequest;
import middle_point_search.backend.domains.place.dto.response.FindPlacesResponse;
import middle_point_search.backend.domains.place.dto.response.SavePlaceResponse;
import middle_point_search.backend.domains.place.repository.PlaceRepository;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.repository.RoomRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

	private final PlaceRepository placeRepository;
	private final RoomRepository roomRepository;
	private final MemberRoomValidateService memberRoomValidateService;
	private final GoogleService googleService;

	// 장소 조회
	public FindPlacesResponse findPlaces(Long memberId, String roomId) {
		// 회원이 방에 속해있는지 확인
		memberRoomValidateService.validateAuthorizedMember(memberId, roomId);

		List<Place> places = placeRepository.findAllByRoom_Id(roomId);

		List<FindPlacesResponse.PlaceVO> myPlaces = places.stream()
			.filter(place -> place.getMember().getId().equals(memberId))
			.map(FindPlacesResponse.PlaceVO::from)
			.toList();

		List<FindPlacesResponse.PlaceVO> friendPlaces = places.stream()
			.filter(place -> !place.getMember().getId().equals(memberId))
			.map(FindPlacesResponse.PlaceVO::from)
			.toList();

		return new FindPlacesResponse(
			!myPlaces.isEmpty(),
			myPlaces,
			!friendPlaces.isEmpty(),
			friendPlaces);
	}

	//장소 저장
	@Transactional(rollbackFor = {CustomException.class})
	public SavePlaceResponse savePlace(String roomId, Member member, SavePlaceRequest request) {
		// 회원이 방에 속해있는지 확인
		memberRoomValidateService.validateAuthorizedMember(member.getId(), roomId);

		Room room = roomRepository.findById(roomId)
			.orElseThrow(() -> CustomException.from(ROOM_NOT_FOUND));

		// 구글 placeId 조회
		String googlePlaceId = googleService.findGooglePlaceId(request.addressLat(), request.addressLong());

		Place place = placeRepository.save(Place.from(request, room, member, googlePlaceId));

		return new SavePlaceResponse(place.getId());
	}

	//장소 업데이트
	@Transactional(rollbackFor = {CustomException.class})
	public void updatePlace(String roomId, Member member, UpdatePlaceRequest request) {
		// 회원이 방에 속해있는지 확인
		memberRoomValidateService.validateAuthorizedMember(member.getId(), roomId);

		// 구글 placeId 조회
		String googlePlaceId = googleService.findGooglePlaceId(request.addressLat(), request.addressLong());

		placeRepository.updatePlace(
			member.getId(),
			request.placeId(),
			googlePlaceId,
			request.siDo(),
			request.siGunGu(),
			request.roadNameAddress(),
			request.addressLat(),
			request.addressLong());
	}

	// 장소 삭제
	@Transactional
	public void deletePlace(Long memberId, Long placeId) {
		Place place = placeRepository.findById(placeId)
			.orElseThrow(() -> CustomException.from(PLACE_NOT_FOUND));

		// 회원이 방에 속해있는지 확인
		memberRoomValidateService.validateAuthorizedMember(memberId, place.getRoom().getId());

		placeRepository.deleteByIdAndRoom_Id(placeId, place.getRoom().getId());
	}
}
