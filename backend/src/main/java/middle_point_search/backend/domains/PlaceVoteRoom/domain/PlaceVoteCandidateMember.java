package middle_point_search.backend.domains.PlaceVoteRoom.domain;

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
    @Column(name = "placeVoteCandidateMember_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "placeVoteCandidate_id")
    private PlaceVoteCandidate placeVoteCandidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
    public PlaceVoteCandidateMember(PlaceVoteCandidate placeVoteCandidate, Member member) {
        this.placeVoteCandidate = placeVoteCandidate;
        this.member = member;
    }

    public void setPlaceVoteCandidate(PlaceVoteCandidate placeVoteCandidate) {
        this.placeVoteCandidate = placeVoteCandidate;
    }
}
