package middle_point_search.backend.domains.PlaceVoteRoom.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.room.domain.Room;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceVoteRoom {

    @Id
    @Column(name = "place_vote_room_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "duplication")
    private Boolean duplication;

    @Column(name = "num_voter")
    private Integer numVoter;

    @Column(name = "url")
    private String url;

    @OneToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @OneToMany(mappedBy = "placeVoteRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PlaceVoteCandidate> placeVoteCandidates;

    //url 생성일때 그냥 방/vote로
    public PlaceVoteRoom(Room room,Boolean duplication, Integer  numVoter){
        this.room =room;
        this.duplication = duplication;
        this.numVoter = numVoter;
    }

    public void setUrl(String url) {
        if (this.url == null) {
            this.url = url;
        }
    }
    public void setPlaceVoteCandidates(List<PlaceVoteCandidate> placeVoteCandidates) {
        this.placeVoteCandidates = placeVoteCandidates;
        for (PlaceVoteCandidate candidate : placeVoteCandidates) {
            candidate.setPlaceVoteRoom(this);
        }
    }

    public void vote(Member member, PlaceVoteCandidate candidate) {
        PlaceVoteCandidateMember voteCandidateMember = new PlaceVoteCandidateMember(candidate, member);
        if (duplication) {
            candidate.addVoter(member);
        } else {
            for (PlaceVoteCandidate c : placeVoteCandidates) {
                c.removeVoter(member);
            }
            candidate.addVoter(member);
        }
    }

    public boolean isDuplication() {
        return duplication;
    }
}

