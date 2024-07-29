package middle_point_search.backend.domains.placeVoteRoom.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import middle_point_search.backend.domains.room.domain.Room;

import java.util.ArrayList;
import java.util.List;

import static middle_point_search.backend.domains.placeVoteRoom.dto.PlaceVoteRoomDTO.PlaceVoteRoomCreateRequest.PlaceCandidateInfo;

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

    public PlaceVoteRoom(Room room, List<PlaceCandidateInfo> candidates) {
        this.room = room;
        this.placeVoteCandidates.clear();
        for (PlaceCandidateInfo candidate : candidates) {
            addPlaceVoteCandidate(candidate);
        }
    }

    public void addPlaceVoteCandidate(PlaceCandidateInfo candidate) {
        this.placeVoteCandidates.add(new PlaceVoteCandidate(candidate, this));
    }
}

