package middle_point_search.backend.domains.TimeVoteRoom.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        private List<List<VoteDateTime>> dateTime;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class VoteDateTime {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime dateTime;

        public VoteDateTime(String dateTime) {
            this.dateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
        public VoteDateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
        }
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

        private List<VoteDateTime> dateTime;
        public static TimeVoteDetail from(String memberName, List<VoteDateTime> dateTime) {
            return new TimeVoteDetail(memberName, dateTime);
        }
    }
}
