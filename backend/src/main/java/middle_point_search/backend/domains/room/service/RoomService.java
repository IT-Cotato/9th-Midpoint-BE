package middle_point_search.backend.domains.room.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.common.exception.errorCode.CommonErrorCode;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.domain.RoomType;
import middle_point_search.backend.domains.room.dto.RoomDTO;
import middle_point_search.backend.domains.room.dto.RoomDTO.RoomCreateRequest;
import middle_point_search.backend.domains.room.dto.RoomDTO.RoomCreateResponse;
import middle_point_search.backend.domains.room.dto.RoomDTO.RoomExistenceCheckResponse;
import middle_point_search.backend.domains.room.dto.RoomDTO.RoomNameResponse;
import middle_point_search.backend.domains.room.repository.RoomRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoomService {

	private final RoomRepository roomRepository;

	//Room 저장하기
	@Transactional
	public RoomCreateResponse createRoom(RoomCreateRequest request) {
		String identityNumber = UUID.randomUUID().toString();
		RoomType roomType = request.getRoomType();

		Room room = Room.from(identityNumber, roomType);

		roomRepository.save(room);

		return RoomCreateResponse.from(room.getIdentityNumber());
	}

	public RoomExistenceCheckResponse checkRoomExistence(String identityNumber) {
		boolean existence = roomRepository.existsByIdentityNumber(identityNumber);

		return RoomExistenceCheckResponse.from(existence);
	}

	//Room 이름 조회
	public RoomNameResponse findRoomName(Room room) {
		String name = room.getRoomName().getKoreanName();

		return RoomNameResponse.from(name);
	}
}
