package middle_point_search.backend.domains.PlaceVoteRoom.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import middle_point_search.backend.domains.member.domain.Member;
import java.util.ArrayList;
import java.util.List;
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceVoteCandidate {

    @Id
    @Column(name = "place_vote_candidate_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_vote_room_id")
    private PlaceVoteRoom placeVoteRoom;

    @OneToMany(mappedBy = "placeVoteCandidate", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<PlaceVoteCandidateMember> voters = new ArrayList<>();
    public PlaceVoteCandidate(String name, PlaceVoteRoom placeVoteRoom) {
        this.name = name;
        this.placeVoteRoom = placeVoteRoom;
    }
    public void setPlaceVoteRoom(PlaceVoteRoom placeVoteRoom) {
        this.placeVoteRoom = placeVoteRoom;
    }

    //투표는 한번만 할 수 있도록
    public void addVoter(Member member) {
        if (!hasVoter(member)) {
            PlaceVoteCandidateMember voteCandidateMember = new PlaceVoteCandidateMember(this, member);
            voters.add(voteCandidateMember);
        }
    }
    public void addUpdateVoter(Member member) {
        PlaceVoteCandidateMember voteCandidateMember = new PlaceVoteCandidateMember(this, member);
        voters.add(voteCandidateMember);
    }
    public void removeVoter(Member member) {
        PlaceVoteCandidateMember toRemove = voters.stream()
                .filter(voter -> voter.getMember().equals(member))
                .findFirst()
                .orElse(null);
        if (toRemove != null) {
            voters.remove(toRemove);
        }
    }
    public boolean hasVoter(Member member) {
        return voters.stream().anyMatch(voter -> voter.getMember().equals(member));
    }
    public int getCount() {
        return voters.size();
    }
}

