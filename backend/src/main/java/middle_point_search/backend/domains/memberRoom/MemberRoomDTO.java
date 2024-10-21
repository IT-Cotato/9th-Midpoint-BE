package middle_point_search.backend.domains.memberRoom;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberRoomDTO {

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class MemberToRoomSaveRequest {

		@NotBlank(message = "roomId는 필수값입니다.")
		private Long roomId;
	}
}
