package middle_point_search.backend.domains.room.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import middle_point_search.backend.domains.room.domain.Room;

public class RoomDTO {

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class RoomCreateResponse {
		private final String id;

		public static RoomCreateResponse from(String id) {
			return new RoomCreateResponse(id);
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class RoomCreateRequest {

		@Parameter(required = true)
		@NotBlank(message = "값이 비어있으면 안 됩니다.")
		private String name;

		private String memo;
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class RoomNameUpdateRequest {

		@Parameter(required = true)
		@NotBlank(message = "값이 비어있으면 안 됩니다.")
		private String name;
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class UpdateRoomMemoRequest {

		private String memo;
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class RoomExistResponse {

		private final boolean existence;

		public static RoomExistResponse from(boolean existence) {
			return new RoomExistResponse(existence);
		}
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class FindRoomDetailResponse {

		private final String name;
		private final String memo;
		private final int memberCount;
		private final List<String> emails;

		public static FindRoomDetailResponse from(Room room, List<String> emails) {
			return new FindRoomDetailResponse(
				room.getName(),
				room.getMemo(),
				emails.size(),
				emails);
		}
	}
}
