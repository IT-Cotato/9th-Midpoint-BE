package middle_point_search.backend.domains.TimeVoteRoom.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

        @JsonCreator
        public TimeVoteRoomVoteRequest(@JsonProperty("dateTime") List<List<String>> dateTime) {
            this.dateTime = new ArrayList<>();
            for (List<String> range : dateTime) {
                List<VoteDateTime> voteDateTimes = new ArrayList<>();
                for (String dt : range) {
                    voteDateTimes.add(new VoteDateTime(dt));
                }
                this.dateTime.add(voteDateTimes);
            }
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class VoteDateTime {

        private LocalDateTime dateTime;

        @JsonCreator
        public VoteDateTime(@JsonProperty("dateTime") String dateTime) {
            this.dateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }

        public String getFormattedDateTime() {
            return this.dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
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
        private List<String> dateTime;

        public static TimeVoteDetail from(String memberName, List<VoteDateTime> dateTime) {
            List<String> formattedDateTimes = new ArrayList<>();
            for (VoteDateTime dt : dateTime) {
                formattedDateTimes.add(dt.getFormattedDateTime());
            }
            return new TimeVoteDetail(memberName, formattedDateTimes);
        }
    }
}
