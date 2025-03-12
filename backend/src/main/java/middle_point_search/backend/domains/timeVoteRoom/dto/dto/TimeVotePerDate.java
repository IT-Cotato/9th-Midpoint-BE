package middle_point_search.backend.domains.timeVoteRoom.dto.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TimeVotePerDate {
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
