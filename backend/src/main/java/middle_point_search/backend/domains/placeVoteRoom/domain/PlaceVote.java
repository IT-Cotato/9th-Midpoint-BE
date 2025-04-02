package middle_point_search.backend.domains.placeVoteRoom.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import middle_point_search.backend.domains.member.domain.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceVote {

	@Id
	@Column(name = "place_vote_candidate_member_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "place_vote_candidate_id")
	private PlaceVoteCandidate placeVoteCandidate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "place_vote_room_id")
	private PlaceVoteRoom placeVoteRoom;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Builder
	private PlaceVote(PlaceVoteCandidate placeVoteCandidate, Member member, PlaceVoteRoom placeVoteRoom) {
		this.placeVoteCandidate = placeVoteCandidate;
		this.member = member;
		this.placeVoteRoom = placeVoteRoom;
	}
}
