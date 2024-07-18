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

    public PlaceVoteRoom(Room room, List<String> names) {
        this.room = room;
        this.placeVoteCandidates.clear();
        List<String> newNames = new ArrayList<>(names);  // 원래 names 리스트를 새 리스트로 복사
        for (String name : newNames) {
            this.placeVoteCandidates.add(new PlaceVoteCandidate(name, this));
        }
    }
}

