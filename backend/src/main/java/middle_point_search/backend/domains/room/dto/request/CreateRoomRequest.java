package middle_point_search.backend.domains.room.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotBlank;

public record CreateRoomRequest(
	@Parameter(required = true) @NotBlank(message = "값이 비어있으면 안 됩니다.") String name,
	String memo
) {
}
