package middle_point_search.backend.domains.place.service;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.memberRoom.MemberRoomValidateService;
import middle_point_search.backend.domains.place.domain.Place;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlaceSaveOrUpdateRequest;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlaceVO;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlacesFindResponse;
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

	//장소 저장
	@Transactional(rollbackFor = {CustomException.class})
	public void savePlace(Long roomId, Member member, PlaceSaveOrUpdateRequest request) {
		// 회원이 방에 속해있는지 확인
		memberRoomValidateService.validateAuthorizedMember(member.getId(), roomId);

		Room room = roomService.findRoom(roomId)
			.orElseThrow(() -> CustomException.from(ROOM_NOT_FOUND));

		placeRepository.save(Place.from(request, room, member));
	}

	// 장소 조회
	public PlacesFindResponse findPlaces(Long memberId, Long roomId) {
		// 회원이 방에 속해있는지 확인
		memberRoomValidateService.validateAuthorizedMember(memberId, roomId);

		List<Place> places = placeRepository.findAllByRoom_Id(roomId);

		List<PlaceVO> placeVOs = places.stream()
			.map(Place::toVO)
			.collect(Collectors.toList());

		boolean existence = !placeVOs.isEmpty();
		return new PlacesFindResponse(existence, placeVOs);
	}

	// 장소 삭제
	@Transactional
	public void deletePlace(Long memberId, Long placeId) {
		// 회원이 방에 속해있는지 확인
		memberRoomValidateService.validateAuthorizedMember(memberId, placeId);

		placeRepository.deleteById(placeId);
	}
}
