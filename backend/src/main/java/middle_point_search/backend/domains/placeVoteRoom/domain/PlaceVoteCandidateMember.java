package middle_point_search.backend.domains.placeVoteRoom.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import middle_point_search.backend.domains.member.domain.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceVoteCandidateMember {

    @Id
    @Column(name = "place_vote_candidate_member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_vote_candidate_id")
    private PlaceVoteCandidate placeVoteCandidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    public PlaceVoteCandidateMember(PlaceVoteCandidate placeVoteCandidate, Member member) {
        this.placeVoteCandidate = placeVoteCandidate;
        this.member = member;
    }

}
