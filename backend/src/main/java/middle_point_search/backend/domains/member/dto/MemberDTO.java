package middle_point_search.backend.domains.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class ProfileUpdateResponse {
		private String profileImageUrl;

		private ProfileUpdateResponse(String profileImageUrl) {
			this.profileImageUrl = profileImageUrl;
		}

		public static ProfileUpdateResponse from(String profileImageUrl) {
			return new ProfileUpdateResponse(profileImageUrl);
		}
	}
}
