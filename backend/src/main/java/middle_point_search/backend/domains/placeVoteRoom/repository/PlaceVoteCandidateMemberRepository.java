package middle_point_search.backend.domains.placeVoteRoom.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteCandidateMember;
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteRoom;

public interface PlaceVoteCandidateMemberRepository extends JpaRepository<PlaceVoteCandidateMember, Long> {

	boolean existsByPlaceVoteCandidate_PlaceVoteRoomAndMember(PlaceVoteRoom placeVoteRoom, Member member);

	Optional<PlaceVoteCandidateMember> findByPlaceVoteCandidate_PlaceVoteRoom_Room_IdAndMember(Long roomId,
		Member member);

	void deleteByPlaceVoteCandidate_PlaceVoteRoomAndMember(PlaceVoteRoom placeVoteRoom, Member member);
}
