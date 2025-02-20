package middle_point_search.backend.common.dummy.dto;

import java.time.LocalDate;

public record MeetingDateDummyDto(
	Long timeVoteRoomId,
	LocalDate date
) {
}
