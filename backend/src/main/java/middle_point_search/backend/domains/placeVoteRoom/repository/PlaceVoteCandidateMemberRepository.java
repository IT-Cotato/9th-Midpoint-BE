package middle_point_search.backend.domains.placeVoteRoom.repository;

import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteCandidateMember;
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteRoom;
import middle_point_search.backend.domains.member.domain.Member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaceVoteCandidateMemberRepository extends JpaRepository<PlaceVoteCandidateMember, Long> {
	boolean existsByPlaceVoteCandidate_PlaceVoteRoomAndMember(PlaceVoteRoom placeVoteRoom, Member member);

	List<PlaceVoteCandidateMember> findAllByPlaceVoteCandidate_PlaceVoteRoomAndMember(PlaceVoteRoom placeVoteRoom,
		Member member);

	Optional<PlaceVoteCandidateMember> findByPlaceVoteCandidate_PlaceVoteRoomAndMember(PlaceVoteRoom placeVoteRoom,
		Member member);
}
