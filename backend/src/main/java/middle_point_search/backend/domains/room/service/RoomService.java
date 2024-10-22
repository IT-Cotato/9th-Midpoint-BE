package middle_point_search.backend.domains.room.service;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.domains.memberRoom.MemberRoomValidateService;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.dto.RoomDTO.RoomCreateRequest;
import middle_point_search.backend.domains.room.dto.RoomDTO.RoomCreateResponse;
import middle_point_search.backend.domains.room.dto.RoomDTO.RoomNameUpdateRequest;
import middle_point_search.backend.domains.room.repository.RoomRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoomService {

	private final RoomRepository roomRepository;
	private final MemberRoomValidateService memberRoomValidateService;

	// Room 저장하기 및 Room에 회원 저장
	@Transactional
	public RoomCreateResponse createRoom(RoomCreateRequest request) {
		Room room = Room.builder()
			.name(request.getName())
			.build();

		// Room저장
		roomRepository.save(room);

		return RoomCreateResponse.from(room.getId());
	}

	// Room 이름 변경하기
	@Transactional(rollbackFor = CustomException.class)
	public void updateRoomName(Long memberId, Long roomId, RoomNameUpdateRequest request) {
		// 회원방 존재 확인
		memberRoomValidateService.validateAuthorizedMember(memberId, roomId);

		// 변경
		Room room = roomRepository.findById(roomId)
			.orElseThrow(() -> CustomException.from(ROOM_NOT_FOUND));
		room.updateName(request.getName());
	}

	// Room 조회
	public Optional<Room> findRoom(Long id) {
		return roomRepository.findById(id);
	}
}
