package middle_point_search.backend.domains.placeVoteRoom.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteRoom;
import middle_point_search.backend.domains.placeVoteRoom.repository.PlaceVoteCandidateMemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceVoteMemberService {

	private final PlaceVoteCandidateMemberRepository placeVoteCandidateMemberRepository;

	public boolean existsByPlaceVote_PlaceVoteRoomAndMember(PlaceVoteRoom placeVoteRoom, Member member) {
		return placeVoteCandidateMemberRepository.existsByPlaceVoteCandidate_PlaceVoteRoomAndMember(placeVoteRoom, member);
	}
}
