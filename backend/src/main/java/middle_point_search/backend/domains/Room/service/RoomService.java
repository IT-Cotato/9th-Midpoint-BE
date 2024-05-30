package middle_point_search.backend.domains.room.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.domains.room.domain.Room;
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
}
