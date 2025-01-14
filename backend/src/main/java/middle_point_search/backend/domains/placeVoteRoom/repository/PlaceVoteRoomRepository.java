package middle_point_search.backend.domains.placeVoteRoom.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteRoom;

public interface PlaceVoteRoomRepository extends JpaRepository<PlaceVoteRoom, Long> {

	boolean existsByRoom_Id(String roomId);

	void deleteByRoom_Id(String roomId);

	Optional<PlaceVoteRoom> findByRoom_Id(String roomId);

}


