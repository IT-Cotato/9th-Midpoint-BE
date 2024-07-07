package middle_point_search.backend.domains.PlaceVoteRoom.repository;

import middle_point_search.backend.domains.PlaceVoteRoom.domain.PlaceVoteCandidateMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceVoteCandidateMemberRepository extends JpaRepository<PlaceVoteCandidateMember, Long> {
}
