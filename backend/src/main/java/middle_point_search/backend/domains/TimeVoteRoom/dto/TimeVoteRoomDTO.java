package middle_point_search.backend.domains.TimeVoteRoom.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class TimeVoteRoomDTO {
    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TimeVoteRoomCreateRequest {
        private  List<LocalDate> dates;
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TimeVoteRoomCreateResponse {
        private final Long id;
        public static TimeVoteRoomCreateResponse from(Long id) {
            return new TimeVoteRoomCreateResponse(id);
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TimeVoteRoomVoteRequest {
        private List<List<String>> dateTime;
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TimeVoteRoomResultResponse {
        private Map<String, List<TimeVoteDetail>> result;
        public static TimeVoteRoomResultResponse from(Map<String, List<TimeVoteDetail>> result) {
            return new TimeVoteRoomResultResponse(result);
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TimeVoteDetail {
        private String memberName;
        private List<String> dateTime;
        public static TimeVoteDetail from(String memberName, List<String> dateTime) {
            return new TimeVoteDetail(memberName, dateTime);
        }
    }
}
