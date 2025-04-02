package middle_point_search.backend.domains.placeVoteRoom.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVote;
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteCandidate;
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteRoom;

public interface PlaceVoteRepository extends JpaRepository<PlaceVote, Long> {

	boolean existsByPlaceVoteCandidate_PlaceVoteRoomAndMember(PlaceVoteRoom placeVoteRoom, Member member);

	Optional<PlaceVote> findByPlaceVoteCandidate_PlaceVoteRoom_Room_IdAndMember_Id(String roomId,
		Long memberId);

	void deleteByPlaceVoteCandidate_PlaceVoteRoomAndMember(PlaceVoteRoom placeVoteRoom, Member member);

	void deleteAllByMemberId(Long memberId);

	void deleteAllByPlaceVoteRoom(PlaceVoteRoom placeVoteRoom);

	List<PlaceVote> findAllByPlaceVoteCandidate(PlaceVoteCandidate placeVoteCandidate);
}
