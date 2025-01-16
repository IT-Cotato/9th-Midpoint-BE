package middle_point_search.backend.domains.member.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdatePasswordRequest(
	@NotBlank(message = "현재 비밀번호를 입력해주세요.")
	String password,
	@NotBlank(message = "새 비밀번호를 입력해주세요.")
	String newPassword
) {
}
