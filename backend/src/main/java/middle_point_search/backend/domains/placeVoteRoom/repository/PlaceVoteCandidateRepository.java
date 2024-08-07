package middle_point_search.backend.domains.placeVoteRoom.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteCandidate;

public interface PlaceVoteCandidateRepository extends JpaRepository<PlaceVoteCandidate, Long> {
	Optional<PlaceVoteCandidate> findById(Long id);
}
