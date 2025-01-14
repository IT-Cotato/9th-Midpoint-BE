package middle_point_search.backend.domains.timeVoteRoom.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import middle_point_search.backend.domains.timeVoteRoom.domain.TimeVoteRoom;

public interface TimeVoteRoomRepository extends JpaRepository<TimeVoteRoom, Long> {
	boolean existsByRoom_Id(String roomId);

	Optional<TimeVoteRoom> findByRoom_Id(String roomId);
}
