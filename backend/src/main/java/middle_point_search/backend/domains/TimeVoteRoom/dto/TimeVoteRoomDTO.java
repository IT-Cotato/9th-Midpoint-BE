package middle_point_search.backend.domains.TimeVoteRoom.dto;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

public class TimeVoteRoomDTO {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
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
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeVoteRoomVoteRequest {
        private List<List<String>> dateTime;
    }
}
