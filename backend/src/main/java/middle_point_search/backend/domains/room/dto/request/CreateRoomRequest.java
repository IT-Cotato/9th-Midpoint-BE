package middle_point_search.backend.domains.room.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRoomRequest(
	@Parameter(required = true) @NotBlank(message = "값이 비어있으면 안 됩니다.") @Size(min = 1, max = 50, message = "방 이름은 최소 1자, 최대 50자 입니다.") String name,
	String memo
) {
}
