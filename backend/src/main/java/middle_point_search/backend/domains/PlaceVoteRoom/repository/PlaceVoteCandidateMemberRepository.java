package middle_point_search.backend.domains.PlaceVoteRoom.repository;

import middle_point_search.backend.domains.PlaceVoteRoom.domain.PlaceVoteCandidateMember;
import middle_point_search.backend.domains.PlaceVoteRoom.domain.PlaceVoteRoom;
import middle_point_search.backend.domains.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceVoteCandidateMemberRepository extends JpaRepository<PlaceVoteCandidateMember, Long> {
    boolean existsByPlaceVoteCandidate_PlaceVoteRoomAndMember(PlaceVoteRoom placeVoteRoom, Member member);
    List<PlaceVoteCandidateMember> findAllByPlaceVoteCandidate_PlaceVoteRoomAndMember(PlaceVoteRoom placeVoteRoom, Member member);
}
