package middle_point_search.backend.domains.room.service;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.domains.room.repository.RoomRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomValidationService {

	private final RoomRepository roomRepository;

	// 방 존재 확인 후 없으면 에러 반환
	public void validateRoomExisting(String roomId) {
		// 방 존재 확인
		if (!roomRepository.existsById(roomId)) {
			throw CustomException.from(ROOM_NOT_FOUND);
		}
	}
}
