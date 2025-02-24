package middle_point_search.backend.domains.timeVoteRoom.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TimeVoteRoomDTO {
	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class CreateTimeVoteRoomRequest {

		@NotEmpty(message = "투표 후보가 제공되지 않았습니다.")
		private List<@NotNull(message = "날짜는 비어있을 수 없습니다.") LocalDate> dates;
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class UpdateTimeVoteRoomRequest {

		@NotEmpty(message = "투표 후보가 제공되지 않았습니다.")
		private List<@NotNull(message = "날짜는 비어있을 수 없습니다.") LocalDate> dates;
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class FindTimeVoteRoomResponse {
		private Boolean existence;
		private List<LocalDate> dates;

		public static FindTimeVoteRoomResponse from(Boolean existence, List<LocalDate> dates) {
			return new FindTimeVoteRoomResponse(existence, dates);
		}
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class CreateTimeVoteRoomResponse {
		private final Long id;

		public static CreateTimeVoteRoomResponse from(Long id) {
			return new CreateTimeVoteRoomResponse(id);
		}
	}
}

