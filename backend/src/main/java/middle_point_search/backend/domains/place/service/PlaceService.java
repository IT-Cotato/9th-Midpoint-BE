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
import middle_point_search.backend.domains.memberRoom.MemberRoomValidateService;
import middle_point_search.backend.domains.place.domain.Place;
import middle_point_search.backend.domains.place.dto.request.ChangeRequest;
import middle_point_search.backend.domains.place.dto.request.ChangeRequest.SavePlaceVO;
import middle_point_search.backend.domains.place.dto.request.ChangeRequest.UpdatePlaceVO;
import middle_point_search.backend.domains.place.dto.response.FindPlacesResponse;
import middle_point_search.backend.domains.place.dto.response.FindPlacesResponse.PlaceVO;
import middle_point_search.backend.domains.place.repository.PlaceRepository;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.service.RoomService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

	private final PlaceRepository placeRepository;
	private final RoomService roomService;
	private final MemberRoomValidateService memberRoomValidateService;
	private final GoogleService googleService;

	//장소 저장, 삭제, 업데이트
	@Transactional(rollbackFor = {CustomException.class})
	public void changePlaces(Long roomId, Member member, ChangeRequest request) {
		// 회원이 방에 속해있는지 확인
		memberRoomValidateService.validateAuthorizedMember(member.getId(), roomId);

		Room room = roomService.findRoom(roomId)
			.orElseThrow(() -> CustomException.from(ROOM_NOT_FOUND));

		// 장소들 저장
		savePlaces(request.getSavePlaces(), room, member);
		// 장소들 업데이트
		updatePlaces(request.getUpdatePlaces(), member.getId());
		// 장소들 삭제
		deletePlaces(request.getDeletePlaces());
	}

	// 장소들 저장
	@Transactional(rollbackFor = {CustomException.class})
	public void savePlaces(List<SavePlaceVO> placeVOS, Room room, Member member) {
		if (placeVOS == null || placeVOS.isEmpty()) {
			return;
		}

		List<Place> places = placeVOS.stream()
			.map(placeVO -> {
				// 구글 placeId 조회
				String googlePlaceId = googleService.findGooglePlaceId(
					placeVO.getAddressLat(),
					placeVO.getAddressLong());

				return Place.from(placeVO, room, member, googlePlaceId);
			})
			.toList();

		placeRepository.saveAll(places);
	}

	// 장소들 업데이트
	@Transactional(rollbackFor = {CustomException.class})
	public void updatePlaces(List<UpdatePlaceVO> placeVOS, Long memberId) {
		if (placeVOS == null || placeVOS.isEmpty()) {
			return;
		}

		placeVOS.forEach(placeVO -> {
			placeRepository.updatePlace(
				memberId,
				placeVO.getPlaceId(),
				placeVO.getSiDo(),
				placeVO.getSiGunGu(),
				placeVO.getRoadNameAddress(),
				placeVO.getAddressLat(),
				placeVO.getAddressLong()
			);
		});
	}

	// 장소들 삭제
	@Transactional
	public void deletePlaces(List<Long> placeIds) {
		if (placeIds == null || placeIds.isEmpty()) {
			return;
		}

		placeRepository.deleteAllByIdIn(placeIds);
	}

	// 장소 조회
	public FindPlacesResponse findPlaces(Long memberId, Long roomId) {
		// 회원이 방에 속해있는지 확인
		memberRoomValidateService.validateAuthorizedMember(memberId, roomId);

		List<Place> places = placeRepository.findAllByRoom_Id(roomId);

		List<PlaceVO> myPlaces = places.stream()
			.filter(place -> place.getMember().getId().equals(memberId))
			.map(PlaceVO::from)
			.toList();

		List<PlaceVO> friendPlaces = places.stream()
			.filter(place -> !place.getMember().getId().equals(memberId))
			.map(PlaceVO::from)
			.toList();

		return new FindPlacesResponse(
			!myPlaces.isEmpty(),
			myPlaces,
			!friendPlaces.isEmpty(),
			friendPlaces);
	}
}
