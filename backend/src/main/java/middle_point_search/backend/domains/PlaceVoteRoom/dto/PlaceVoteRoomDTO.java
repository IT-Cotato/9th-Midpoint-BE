package middle_point_search.backend.domains.PlaceVoteRoom.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


public class PlaceVoteRoomDTO {

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PlaceVoteRoomCreateResponse {
        private final Long id;
        private final String url;
        public static PlaceVoteRoomCreateResponse from(Long id, String url) {
            return new PlaceVoteRoomCreateResponse(id, url);
        }
    }
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PlaceVoteInfoResponse {
        private List<PlaceVoteCandidateInfo> placeCandidates;
        private int numVoter;
        private boolean duplication;

        @Getter
        @AllArgsConstructor
        @NoArgsConstructor
        public static class PlaceVoteCandidateInfo {
            private Long id;
            private String name;
            private int count;
            private List<String> voters;
        }
    }
}

