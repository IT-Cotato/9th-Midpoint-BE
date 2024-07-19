package middle_point_search.backend.domains.PlaceVoteRoom.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    public int getCount() {
        return voters.size();
    }
}

