package middle_point_search.backend.domains.TimeVoteRoom.repository;

import middle_point_search.backend.domains.TimeVoteRoom.domain.TimeVoteRoom;
import middle_point_search.backend.domains.room.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TimeVoteRoomRepository extends JpaRepository<TimeVoteRoom, Long> {
    boolean existsByRoom(Room room);
    Optional<TimeVoteRoom> findByRoom(Room room);
}
