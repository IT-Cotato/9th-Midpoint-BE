package middle_point_search.backend.domains.timeVoteRoom.dto.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeVoteDetail {
	private String memberName;
	private List<TimeRange> dateTime;

	public static TimeVoteDetail from(String memberName, List<TimeRange> dateTime) {
		return new TimeVoteDetail(memberName, dateTime);
	}
}
