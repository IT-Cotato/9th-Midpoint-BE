package middle_point_search.backend.domains.placeVoteRoom.service;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;
import static middle_point_search.backend.domains.placeVoteRoom.dto.PlaceVoteRoomDTO.*;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.domains.memberRoom.MemberRoomValidateService;
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteCandidate;
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteRoom;
import middle_point_search.backend.domains.placeVoteRoom.repository.PlaceVoteRoomRepository;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.service.RoomService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceVoteRoomService {

	private final PlaceVoteRoomRepository placeVoteRoomRepository;
	private final RoomService roomService;
	private final MemberRoomValidateService memberRoomValidateService;

	// 장소투표방 생성
	@Transactional(rollbackFor = {CustomException.class})
	public PlaceVoteRoomCreateResponse createPlaceVoteRoom(Long memberId, Long roomId,
		PlaceVoteRoomCreateRequest request) {
		// 방에 대한 회원인지 확인
		memberRoomValidateService.validateAuthorizedMember(memberId, roomId);

		// 장소투표방 존재여부 확인
		validateDuplicatePlaceVoteRoom(roomId);

		// 방 조회
		Room room = roomService.findRoom(roomId)
			.orElseThrow(() -> CustomException.from(ROOM_NOT_FOUND));

		// 장소투표방 엔티티 생성 및 저장
		PlaceVoteRoom placeVoteRoom = new PlaceVoteRoom(room);
		request.getPlaceCandidates().stream()
			.map(placeCandidateInfo -> new PlaceVoteCandidate(placeCandidateInfo, placeVoteRoom))
			.forEach(placeVoteRoom::addPlaceVoteCandidate);

		PlaceVoteRoom savedPlaceVoteRoom = placeVoteRoomRepository.save(placeVoteRoom);

		return PlaceVoteRoomCreateResponse.from(savedPlaceVoteRoom.getId());
	}

	//장소투표방 리셋
	@Transactional(rollbackFor = {CustomException.class})
	public void UpdatePlaceVoteRoom(Long memberId, Long roomId, PlaceVoteRoomCreateRequest request) {
		// 방에 대한 회원인지 확인
		memberRoomValidateService.validateAuthorizedMember(memberId, roomId);

		PlaceVoteRoom placeVoteRoom = placeVoteRoomRepository.findByRoom_Id(roomId)
			.orElseThrow(() -> CustomException.from(PLACE_VOTE_ROOM_NOT_FOUND));

		// 장소투표방 리셋
		placeVoteRoom.resetPlaceVoteRoom();

		// 장소투표 후보 추가
		request.getPlaceCandidates().stream()
			.map(placeCandidateInfo -> new PlaceVoteCandidate(placeCandidateInfo, placeVoteRoom))
			.forEach(placeVoteRoom::addPlaceVoteCandidate);
	}

	// 장소투표방 조회
	public Optional<PlaceVoteRoom> findByRoomId(Long roomId) {
		return placeVoteRoomRepository.findByRoom_Id(roomId);
	}

	// 장소투표방 중복 체크
	private void validateDuplicatePlaceVoteRoom(Long roomId) {
		boolean exists = placeVoteRoomRepository.existsByRoom_Id(roomId);
		if (exists) {
			throw CustomException.from(DUPLICATE_VOTE_ROOM);
		}
	}
}