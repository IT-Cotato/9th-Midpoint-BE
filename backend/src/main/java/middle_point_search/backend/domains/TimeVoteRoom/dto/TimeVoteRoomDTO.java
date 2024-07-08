package middle_point_search.backend.domains.TimeVoteRoom.dto;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

public class TimeVoteRoomDTO {
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TimeVoteRoomCreateRequest {
        private final String roomId;
        private final String meetingName;
        private final List<LocalDate> dates;

    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TimeVoteRoomCreateResponse {
        private final Long id;
        private final String url;

        public static TimeVoteRoomCreateResponse from(Long id, String url) {
            return new TimeVoteRoomCreateResponse(id, url);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeVoteRoomVoteRequest {
        private List<List<String>> dateTime;
    }
}
