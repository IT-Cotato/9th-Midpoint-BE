package middle_point_search.backend.domains.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VerifyEmailVerificationCodeRequest {

	@NotBlank(message = "이메일은 필수값입니다.")
	private String email;

	@NotBlank(message = "인증코드는 필수값입니다.")
	private String code;
}
