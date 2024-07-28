package middle_point_search.backend.domains.TimeVoteRoom.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        private List<TimeRange> dateTime;

        public TimeVoteRoomVoteRequest(List<TimeRange> dateTime) {
            this.dateTime = dateTime;
        }
    }


    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TimeVoteRoomResultResponse {
        private Map<String, List<TimeVoteDetail>> result;
        private int totalMemberNum;
        public static TimeVoteRoomResultResponse from(Map<String, List<TimeVoteDetail>> result, int totalMemberNum) {
            return new TimeVoteRoomResultResponse(result, totalMemberNum);
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TimeVoteDetail {
        private String memberName;
        private List<TimeRange> dateTime;

        public static TimeVoteDetail from(String memberName, List<TimeRange> dateTime) {
            return new TimeVoteDetail(memberName, dateTime);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class TimeRange {

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
        @Schema(type = "string", example = "2024-07-26 13:00")
        private LocalDateTime memberAvailableStartTime;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
        @Schema(type = "string", example = "2024-07-26 19:00")
        private LocalDateTime memberAvailableEndTime;
    }
}

