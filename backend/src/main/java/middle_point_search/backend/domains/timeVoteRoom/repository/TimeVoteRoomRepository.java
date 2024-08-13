package middle_point_search.backend.domains.timeVoteRoom.repository;

import middle_point_search.backend.domains.timeVoteRoom.domain.TimeVoteRoom;
import middle_point_search.backend.domains.room.domain.Room;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TimeVoteRoomRepository extends JpaRepository<TimeVoteRoom, Long> {
	boolean existsByRoom(Room room);

	Optional<TimeVoteRoom> findByRoom_IdentityNumber(String identityNumber);

	Optional<TimeVoteRoom> findByRoom(Room room);
}
