package middle_point_search.backend.domains.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SendNewPasswordRequest {

	@NotBlank(message = "email은 비어있을 수 없습니다.")
	@Email(message = "email 형식이 올바르지 않습니다.")
	private String email;

	@NotBlank(message = "code는 비어있을 수 없습니다.")
	private String code;
}
