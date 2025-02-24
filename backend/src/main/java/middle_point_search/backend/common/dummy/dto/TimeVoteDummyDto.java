package middle_point_search.backend.common.dummy.dto;

import java.time.LocalDateTime;

public record TimeVoteDummyDto(
	Long timeVoteRoomId,
	Long memberId,
	Long meetingDateId,
	LocalDateTime memberAvailableStartTime,
	LocalDateTime memberAvailableEndTime
) {
}
