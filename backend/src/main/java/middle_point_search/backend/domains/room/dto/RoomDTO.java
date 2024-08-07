package middle_point_search.backend.domains.room.dto;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import middle_point_search.backend.domains.room.domain.RoomType;

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
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class RoomExistenceCheckResponse {
		private Boolean existence;

		public static RoomExistenceCheckResponse from(Boolean existence) {
			return new RoomExistenceCheckResponse(existence);
		}
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class RoomNameResponse {
		private String name;

		public static RoomNameResponse from(String name) {
			return new RoomNameResponse(name);
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class RoomCreateRequest {

		@Parameter(required = true)
		@NotNull(message = "값이 비어있으면 안 됩니다.")
		private RoomType roomType;
	}
}
