package middle_point_search.backend.domains.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateMemberRequest(
	@NotBlank(message = "이름을 입력해주세요.") @Size(min = 2, max = 30, message = "이름은 2자 이상, 30자 이하여야 합니다.") String name,
	@Email(message = "이메일 형식이 아닙니다.") @NotBlank(message = "이메일을 입력해주세요") @Size(max = 254, message = "이메일은 최대 254자 입니다.") String email,
	@NotBlank(message = "비밀번호를 입력해주세요.") @Size(max = 20, message = "비밀번호는 최대 20자 입니다.") String pw,
	@NotNull(message = "주소가 있는 지 여부를 입력해주세요.") Boolean existAddress,
	String siDo,
	String siGunGu,
	String roadNameAddress,
	Double addressLatitude,
	Double addressLongitude,
	@NotBlank(message = "인증코드를 입력해주세요.") String code
) {
}