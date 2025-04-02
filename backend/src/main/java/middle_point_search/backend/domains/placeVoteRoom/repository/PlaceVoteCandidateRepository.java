package middle_point_search.backend.domains.placeVoteRoom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteCandidate;
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteRoom;

public interface PlaceVoteCandidateRepository extends JpaRepository<PlaceVoteCandidate, Long> {

	void deleteAllByPlaceVoteRoom(PlaceVoteRoom placeVoteRoom);

	List<PlaceVoteCandidate> findAllByPlaceVoteRoom(PlaceVoteRoom placeVoteRoom);
}
