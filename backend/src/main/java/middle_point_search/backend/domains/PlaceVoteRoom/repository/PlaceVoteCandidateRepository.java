package middle_point_search.backend.domains.PlaceVoteRoom.repository;

import middle_point_search.backend.domains.PlaceVoteRoom.domain.PlaceVoteCandidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceVoteCandidateRepository extends JpaRepository<PlaceVoteCandidate, Long> {
    Optional<PlaceVoteCandidate> findById(Long id);
}
