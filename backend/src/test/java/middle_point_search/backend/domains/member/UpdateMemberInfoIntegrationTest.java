package middle_point_search.backend.domains.member;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import middle_point_search.backend.domains.BaseIntegrationTest;
import middle_point_search.backend.domains.member.dto.AccessTokenAndRefreshToken;
import middle_point_search.backend.domains.member.dto.request.UpdatePasswordRequest;
import middle_point_search.backend.domains.member.repository.MemberRepository;

@DisplayName("회원 정보 수정")
public class UpdateMemberInfoIntegrationTest extends BaseIntegrationTest {

	private String accessToken;

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private MemberRepository memberRepository;

	@BeforeEach
	public void setUp() throws Exception {
		AccessTokenAndRefreshToken accessTokenAndRefreshToken = signupAndLogin();
		accessToken = accessTokenAndRefreshToken.accessToken();
	}

	@Nested
	@DisplayName("비밀번호 변경")
	class 비밀번호_변경 {
		@Test
		@DisplayName("비밀번호 수정에 성공한다.")
		public void 비밀번호수정성공() throws Exception {
			// given
			String newPw = "5678";

			UpdatePasswordRequest request = new UpdatePasswordRequest(MEMBER_PW, newPw);

			// when
			mockMvc.perform(patch("/api/members/password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header("Authorization", "Bearer " + accessToken)
				.accept(MediaType.APPLICATION_JSON)
			);

			// then
			// 변경된 비밀번호가 저장되었는지
			memberRepository.findByEmail(MEMBER_EMAIL).ifPresent(member -> {
				boolean isMatch = passwordEncoder.matches(newPw, member.getPw());
				assertThat(isMatch).isTrue();
			});
		}

		@ParameterizedTest
		@MethodSource("provideInvalidUpdatePasswordRequest")
		@DisplayName("현재 비밀번호가 일치하지 않으면 비밀번호 수정에 실패한다.")
		public void 비밀번호일치하지않음_비밀번호수정실패() throws Exception {

			UpdatePasswordRequest request = new UpdatePasswordRequest("wrongPw", "5678");

			// when
			ResultActions resultActions = mockMvc.perform(patch("/api/members/password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header("Authorization", "Bearer " + accessToken)
				.accept(MediaType.APPLICATION_JSON)
			);

			// then
			resultActions.andExpect(jsonPath("$.code").value("M-005"));
		}

		@ParameterizedTest
		@MethodSource("provideInvalidUpdatePasswordRequest")
		@DisplayName("새 비밀번호가 형식이 맞지 않으면 비밀번호 수정에 실패한다.")
		public void 비밀번호형식오류_비밀번호수정실패(UpdatePasswordRequest request) throws Exception {

			// when
			ResultActions resultActions = mockMvc.perform(patch("/api/members/password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header("Authorization", "Bearer " + accessToken)
				.accept(MediaType.APPLICATION_JSON)
			);

			// then
			resultActions.andExpect(jsonPath("$.code").value("C-202"));
		}

		private static Stream<UpdatePasswordRequest> provideInvalidUpdatePasswordRequest() {
			return Stream.of(
				new UpdatePasswordRequest(MEMBER_PW, null),
				new UpdatePasswordRequest(MEMBER_PW, ""),
				new UpdatePasswordRequest(MEMBER_PW, "012345678901234567891")
			);
		}
	}
}
