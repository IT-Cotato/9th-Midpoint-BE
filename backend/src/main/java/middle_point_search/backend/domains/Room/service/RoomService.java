package middle_point_search.backend.domains.Room.service;

import static middle_point_search.backend.domains.Room.dto.RoomDTO.*;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.domains.Room.domain.Room;
import middle_point_search.backend.domains.Room.dto.RoomDTO;
import middle_point_search.backend.domains.Room.repository.RoomRepository;

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
}
