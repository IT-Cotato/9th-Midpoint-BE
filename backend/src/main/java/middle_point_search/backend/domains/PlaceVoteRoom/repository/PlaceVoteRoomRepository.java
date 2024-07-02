package middle_point_search.backend.domains.PlaceVoteRoom.repository;

import middle_point_search.backend.domains.PlaceVoteRoom.domain.PlaceVoteRoom;
import middle_point_search.backend.domains.room.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceVoteRoomRepository extends JpaRepository<PlaceVoteRoom, Long> {

    Optional<PlaceVoteRoom> findByRoom(Room room);
}


