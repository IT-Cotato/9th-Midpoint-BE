package middle_point_search.backend.domains.PlaceVoteRoom.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;


public class PlaceVoteRoomDTO {
    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PlaceVoteRoomCreateRequest {
        private List<PlaceCandidateInfo> placeCandidates;

        @Getter
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        public static class PlaceCandidateInfo {
            private String name;
            private String siDo;
            private String siGunGu;
            private String roadNameAddress;
            private Double addressLat;
            private Double addressLong;
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PlaceVoteRoomCreateResponse {
        private final Long id;
        public static PlaceVoteRoomCreateResponse from(Long id) {
            return new PlaceVoteRoomCreateResponse(id);
        }
    }

    @Getter
    @AllArgsConstructor
    public static class PlaceVoteInfoResponse {
        private List<PlaceVoteCandidateInfo> placeCandidates;

        @Getter
        @AllArgsConstructor
        public static class PlaceVoteCandidateInfo {
            private Long id;
            private String name;
            private String siDo;
            private String siGunGu;
            private String roadNameAddress;
            private Double addressLat;
            private Double addressLong;
            private int count;
            private List<String> voters;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PlaceVoteRequest{
       private  Long choicePlace;
    }
}

