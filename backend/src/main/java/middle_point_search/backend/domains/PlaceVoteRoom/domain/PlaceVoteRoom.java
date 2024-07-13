package middle_point_search.backend.domains.PlaceVoteRoom.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import middle_point_search.backend.domains.room.domain.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private List<PlaceVoteCandidate> placeVoteCandidates = new ArrayList<>();

    public PlaceVoteRoom(Room room, List<String> names){
        this.room =room;
        this.placeVoteCandidates=names.stream()
                .map(candidateName -> new PlaceVoteCandidate(candidateName, this))
                .collect(Collectors.toList());;
    }
}

