package middle_point_search.backend.domains.placeVoteRoom.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteRoom;
import middle_point_search.backend.domains.room.domain.Room;

public interface PlaceVoteRoomRepository extends JpaRepository<PlaceVoteRoom, Long> {

	boolean existsByRoom_Id(Long roomId);

	void deleteByRoom_Id(Long roomId);

	Optional<PlaceVoteRoom> findByRoom_Id(Long roomId);

}


