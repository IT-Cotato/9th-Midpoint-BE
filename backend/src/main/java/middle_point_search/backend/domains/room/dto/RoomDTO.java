package middle_point_search.backend.domains.room.dto;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RoomDTO {

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class RoomCreateResponse {
		private final Long id;

		public static RoomCreateResponse from(Long id) {
			return new RoomCreateResponse(id);
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class RoomCreateRequest {

		@Parameter(required = true)
		@NotBlank(message = "값이 비어있으면 안 됩니다.")
		private String name;
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class RoomNameUpdateRequest {

		@Parameter(required = true)
		@NotBlank(message = "값이 비어있으면 안 됩니다.")
		private final String name;
	}
}
