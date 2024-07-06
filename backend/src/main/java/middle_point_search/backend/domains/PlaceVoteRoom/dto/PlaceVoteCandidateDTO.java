package middle_point_search.backend.domains.PlaceVoteRoom.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PlaceVoteCandidateDTO {
    private final Long id;
    private final String name;
    public static PlaceVoteCandidateDTO from(Long id, String name) {
        return new PlaceVoteCandidateDTO(id, name);
    }

}
