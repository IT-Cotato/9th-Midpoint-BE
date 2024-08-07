package middle_point_search.backend.domains.placeVoteRoom.repository;

import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteRoom;
import middle_point_search.backend.domains.room.domain.Room;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceVoteRoomRepository extends JpaRepository<PlaceVoteRoom, Long> {

	boolean existsByRoom(Room room);

	boolean existsByRoomIdentityNumber(String identityNumber);

	Optional<PlaceVoteRoom> findByRoom(Room room);
}


