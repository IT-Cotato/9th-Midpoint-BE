package middle_point_search.backend.domains.timeVoteRoom.dto.response;

import java.time.LocalDate;
import java.util.List;

public record FindTimeVoteRoomResponse(
	Boolean existence,
	List<LocalDate> dates
) {
	public static FindTimeVoteRoomResponse from(Boolean existence, List<LocalDate> dates) {
		return new FindTimeVoteRoomResponse(existence, dates);
	}
}
