package middle_point_search.backend.domains.timeVoteRoom.dto.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeVoteDetail {
	private String memberName;
	private TimeRange dateTime;

	public static TimeVoteDetail from(String memberName, TimeRange dateTime) {
		return new TimeVoteDetail(memberName, dateTime);
	}
}
