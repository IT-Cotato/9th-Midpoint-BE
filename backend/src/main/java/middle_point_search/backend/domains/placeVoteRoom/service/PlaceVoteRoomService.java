package middle_point_search.backend.domains.placeVoteRoom.service;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;
import static middle_point_search.backend.domains.placeVoteRoom.dto.PlaceVoteRoomDTO.*;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
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

	// 장소투표방 생성
	@Transactional(rollbackFor = {CustomException.class})
	public PlaceVoteRoomCreateResponse createPlaceVoteRoom(Long roomId, PlaceVoteRoomCreateRequest request) {
		// 장소투표방 존재여부 확인
		boolean exists = placeVoteRoomRepository.existsByRoom_Id(roomId);
		if (exists) {
			throw CustomException.from(DUPLICATE_VOTE_ROOM);
		}

		// 방 조회
		Room room = roomService.findRoom(roomId)
			.orElseThrow(() -> CustomException.from(ROOM_NOT_FOUND));

		PlaceVoteRoom placeVoteRoom = new PlaceVoteRoom(room, request.getPlaceCandidates());
		PlaceVoteRoom savedPlaceVoteRoom = placeVoteRoomRepository.save(placeVoteRoom);

		return PlaceVoteRoomCreateResponse.from(savedPlaceVoteRoom.getId());
	}

	//장소투표방 재생성
	@Transactional(rollbackFor = {CustomException.class})
	public PlaceVoteRoomCreateResponse recreatePlaceVoteRoom(Long roomId, PlaceVoteRoomCreateRequest request) {
		// 기존 투표방 삭제
		placeVoteRoomRepository.deleteByRoom_Id(roomId);

		// 방 조회
		Room room = roomService.findRoom(roomId)
			.orElseThrow(() -> CustomException.from(ROOM_NOT_FOUND));

		// 투표방 생성
		PlaceVoteRoom placeVoteRoom = new PlaceVoteRoom(room, request.getPlaceCandidates());
		PlaceVoteRoom savedPlaceVoteRoom = placeVoteRoomRepository.save(placeVoteRoom);

		return PlaceVoteRoomCreateResponse.from(savedPlaceVoteRoom.getId());
	}

	public Optional<PlaceVoteRoom> findById(Long placeVoteRoomId) {
		return placeVoteRoomRepository.findById(placeVoteRoomId);
	}

	public Optional<PlaceVoteRoom> findByRoomId(Long roomId) {
		return placeVoteRoomRepository.findByRoom_Id(roomId);
	}
}