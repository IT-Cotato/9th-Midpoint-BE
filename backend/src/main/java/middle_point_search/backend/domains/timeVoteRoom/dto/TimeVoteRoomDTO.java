package middle_point_search.backend.domains.timeVoteRoom.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

		@NotEmpty(message = "투표 후보가 제공되지 않았습니다.")
		private List<@NotNull(message = "날짜는 비어있을 수 없습니다.") LocalDate> dates;
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class TimeVoteRoomGetResponse {
		private Boolean existence;
		private List<LocalDate> dates;

		public static TimeVoteRoomGetResponse from(Boolean existence, List<LocalDate> dates) {
			return new TimeVoteRoomGetResponse(existence, dates);
		}
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

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class VotedAndVoteItemsGetResponse {
		private Boolean myVotesExistence;
		private List<TimeRange> myVotes;
		private Boolean otherVotesExistence;
		private List<TimeVotePerDate> otherVotes;

		public static VotedAndVoteItemsGetResponse from(
			Boolean myVotesExistence,
			List<TimeRange> myVotes,
			Boolean otherVotesExistence,
			List<TimeVotePerDate> otherVotes) {

			return new VotedAndVoteItemsGetResponse(myVotesExistence, myVotes, otherVotesExistence, otherVotes);
		}
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class TimeVotePerDate {
		private LocalDate date;
		private List<TimeVotePerDateDetail> timeVotes;

		@Getter
		@AllArgsConstructor(access = AccessLevel.PRIVATE)
		public static class TimeVotePerDateDetail {
			private String memberName;
			private TimeRange dateTime;

			public static TimeVotePerDateDetail from(String memberName, TimeRange dateTime) {
				return new TimeVotePerDateDetail(memberName, dateTime);
			}
		}

		public static TimeVotePerDate from(LocalDate date, List<TimeVotePerDateDetail> timeVotePerDateDetails) {
			return new TimeVotePerDate(date, timeVotePerDateDetails);
		}
	}
}

