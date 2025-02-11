package middle_point_search.backend.domains.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateMemberNameRequest(
	@NotBlank(message = "이름을 입력해주세요.") @Size(min = 2, max = 30, message = "이름은 2자 이상, 30자 이하여야 합니다.") String name
) {
}
