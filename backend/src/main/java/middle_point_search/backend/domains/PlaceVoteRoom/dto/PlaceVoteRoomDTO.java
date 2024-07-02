package middle_point_search.backend.domains.PlaceVoteRoom.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;


public class PlaceVoteRoomDTO {

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PlaceVoteRoomCreateResponse {
        private final Long id;
        private final String url;
     //   private final List<PlaceVoteCandidateDTO> candidates;

        public static PlaceVoteRoomCreateResponse from(Long id, String url) {
            return new PlaceVoteRoomCreateResponse(id, url);
        }
    }
}
