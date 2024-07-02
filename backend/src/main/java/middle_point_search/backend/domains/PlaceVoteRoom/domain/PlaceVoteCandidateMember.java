package middle_point_search.backend.domains.PlaceVoteRoom.domain;

import jakarta.persistence.*;
import middle_point_search.backend.domains.member.domain.Member;

public class PlaceVoteCandidateMember {

    @Id
    @Column(name = "placeVoteCandidateMember_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name ="MEMBER_ID")
    private Member member;


    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "placeVoteCandidate_id")
    private PlaceVoteCandidate placeVoteCandidate;



    public PlaceVoteCandidateMember(PlaceVoteCandidate placeVoteCandidate, Member member){
        this.placeVoteCandidate= placeVoteCandidate;
        this.member=member;
    }

}
