package middle_point_search.backend.domains.PlaceVoteRoom.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PlaceVoteRoomRequestDTO {

    private String roomId;
    private List<String> placeCandidates;
    private boolean duplication;
    private int numVoter;
}
