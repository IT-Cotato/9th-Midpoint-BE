package middle_point_search.backend.domains.timeVoteRoom.dto.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateTimeVoteRoomRequest(
	@NotEmpty(message = "투표 후보가 제공되지 않았습니다.")
	List<@NotNull(message = "날짜는 비어있을 수 없습니다.") LocalDate> dates
) {}
