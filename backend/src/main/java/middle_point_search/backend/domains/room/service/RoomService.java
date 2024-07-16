package middle_point_search.backend.domains.room.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.common.exception.errorCode.CommonErrorCode;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.domain.RoomType;
import middle_point_search.backend.domains.room.dto.RoomDTO.RoomCreateResponse;
import middle_point_search.backend.domains.room.dto.RoomDTO.RoomExistenceCheckResponse;
import middle_point_search.backend.domains.room.repository.RoomRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoomService {

	private final RoomRepository roomRepository;

	@Transactional
	public RoomCreateResponse createRoom() {
		String identityNumber = UUID.randomUUID().toString();

		Room room = Room.from(identityNumber);

		roomRepository.save(room);

		return RoomCreateResponse.from(room.getIdentityNumber());
	}

	public RoomExistenceCheckResponse checkRoomExistence(String identityNumber) {
		boolean existence = roomRepository.existsByIdentityNumber(identityNumber);

		return RoomExistenceCheckResponse.from(existence);
	}

	@Transactional
	public void updateRoomType(Room room, RoomType roomType) {
		//1.방 type이 disable로 지정이 안되어 있을 경우에만 변경
		//2.방 type과 다른 방타입으로 요청이 왔으면 에러 리턴
		//3.그 외는 패스
		if (room.getRoomType() == RoomType.DISABLED) {
			room.updateRoomType(roomType);
		} else if (roomType != room.getRoomType()) {
			throw new CustomException(CommonErrorCode.BAD_REQUEST);
		}
	}
}
