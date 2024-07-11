package middle_point_search.backend.domains.PlaceVoteRoom.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PlaceVoteRequestDTO {
    private List<Long> choicePlaces;
}
