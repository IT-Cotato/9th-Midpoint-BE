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

    @OneToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @OneToMany(mappedBy = "placeVoteRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PlaceVoteCandidate> placeVoteCandidates;


    public PlaceVoteRoom(Room room){
        this.room =room;
    }
    public void setPlaceVoteCandidates(List<PlaceVoteCandidate> placeVoteCandidates) {
        this.placeVoteCandidates = placeVoteCandidates;
        for (PlaceVoteCandidate candidate : placeVoteCandidates) {
            candidate.setPlaceVoteRoom(this);
        }
    }
}

