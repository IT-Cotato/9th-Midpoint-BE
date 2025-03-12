package middle_point_search.backend.domains.timeVoteRoom.dto.request;

import java.util.List;

import middle_point_search.backend.domains.timeVoteRoom.dto.dto.TimeRange;

public record VoteRequest(
	List<TimeRange> dateTime
) {
}