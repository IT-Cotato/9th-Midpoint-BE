package middle_point_search.backend.domains.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequest(
	@NotBlank(message = "현재 비밀번호를 입력해주세요.")
	String password,
	@NotBlank(message = "새 비밀번호를 입력해주세요.") @Size(max = 20, message = "비밀번호는 최대 20자 입니다.")
	String newPassword
) {
}
