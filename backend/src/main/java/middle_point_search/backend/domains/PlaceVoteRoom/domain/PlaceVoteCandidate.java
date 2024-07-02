package middle_point_search.backend.domains.PlaceVoteRoom.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceVoteCandidate {

    @Id
    @Column(name = "placeVoteCandidate_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "name")
    private String name;

    //이거 넣는게 맞는거 같아
    private int count;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "placeVoteRoom_id") //이렇게 하는게 맞나?
    private PlaceVoteRoom placeVoteRoom;


    public PlaceVoteCandidate(String name, PlaceVoteRoom placeVoteRoom) {
        this.name = name;
        this.placeVoteRoom = placeVoteRoom;
    }

    @Builder
    public PlaceVoteCandidate(PlaceVoteRoom placeVoteRoom, String name){
        this.placeVoteRoom = placeVoteRoom;
        this.name=name;
        this.count = 0;

    }

    public void vote() {
        count++;
    }
    public void setPlaceVoteRoom(PlaceVoteRoom placeVoteRoom) {
        this.placeVoteRoom = placeVoteRoom;
    }

}