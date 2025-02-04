package middle_point_search.backend.domains.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMemberRequest(
	@NotBlank(message = "이름을 입력해주세요.") String name,
	@Email(message = "이메일 형식이 아닙니다.") String email,
	@NotBlank(message = "비밀번호를 입력해주세요.") String pw,
	@NotNull(message = "주소가 있는 지 여부를 입력해주세요.") Boolean existAddress,
	String siDo,
	String siGunGu,
	String roadNameAddress,
	Double addressLatitude,
	Double addressLongitude,
	@NotBlank(message = "인증코드를 입력해주세요.") String code
) {}