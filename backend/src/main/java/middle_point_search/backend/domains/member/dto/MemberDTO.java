package middle_point_search.backend.domains.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberDTO {

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class MemberCreateRequest {

		@NotBlank(message = "이름을 입력해주세요.")
		private String name;

		@Email(message = "이메일 형식이 아닙니다.")
		private String email;

		@NotBlank(message = "비밀번호를 입력해주세요.")
		private String pw;

		@NotNull(message = "주소가 있는 지 여부를 입력해주세요.")
		private Boolean existAddress;

		//주소
		private String siDo;
		private String siGunGu;
		private String roadNameAddress;
		private Double addressLatitude;
		private Double addressLongitude;
	}
}
