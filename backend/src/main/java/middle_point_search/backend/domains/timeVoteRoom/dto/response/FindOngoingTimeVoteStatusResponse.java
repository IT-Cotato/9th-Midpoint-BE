package middle_point_search.backend.domains.timeVoteRoom.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import middle_point_search.backend.domains.timeVoteRoom.dto.dto.TimeRange;
import middle_point_search.backend.domains.timeVoteRoom.dto.dto.TimeVotePerDate;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FindOngoingTimeVoteStatusResponse {
	private Boolean myVotesExistence;
	private List<TimeRange> myVotes;
	private Boolean otherVotesExistence;
	private List<TimeVotePerDate> otherVotes;

	public static FindOngoingTimeVoteStatusResponse from(
		Boolean myVotesExistence,
		List<TimeRange> myVotes,
		Boolean otherVotesExistence,
		List<TimeVotePerDate> otherVotes) {

		return new FindOngoingTimeVoteStatusResponse(myVotesExistence, myVotes, otherVotesExistence, otherVotes);
	}
}