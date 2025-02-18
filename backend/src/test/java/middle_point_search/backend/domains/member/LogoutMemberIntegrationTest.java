package middle_point_search.backend.domains.member;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import middle_point_search.backend.common.BaseIntegrationTest;
import middle_point_search.backend.common.dto.AccessTokenAndRefreshToken;
import middle_point_search.backend.domains.member.repository.MemberRepository;

@DisplayName("로그아웃")
public class LogoutMemberIntegrationTest extends BaseIntegrationTest {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private String accessToken;
	private String refreshToken;

	@BeforeEach
	public void setUp() throws Exception {
		// 회원가입 및 로그인
		AccessTokenAndRefreshToken accessTokenAndRefreshToken = signupAndLoginNoAddressMember();
		accessToken = accessTokenAndRefreshToken.accessToken();
		refreshToken = accessTokenAndRefreshToken.refreshToken();
	}

	@Test
	@DisplayName("로그아웃에 성공한다.")
	public void 로그아웃성공() throws Exception {
		// when
		ResultActions resultActions = mockMvc.perform(post("/api/members/logout")
			.header("Authorization", "Bearer " + accessToken));

		// then
		resultActions
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("accessToken이 일치하지 않으면 로그아웃에 실패한다.")
	public void accessToken일치하지않음_로그아웃실패() throws Exception {
		String wrongAccessToken = "wrongAccessToken";

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/members/logout")
			.header("Authorization", "Bearer " + wrongAccessToken));

		// then
		// accessToken이 일치하지 않으면, 기간이 만료된 것이라 판단
		resultActions
			.andExpect(jsonPath("$.code").value("A-004"));
	}

	@Test
	@DisplayName("로그아웃 후에는 refreshToken으로 재발급 불가능하다.")
	public void refreshToken으로재발급불가능() throws Exception {
		// given
		// 로그아웃
		mockMvc.perform(post("/api/members/logout")
			.header("Authorization", "Bearer " + accessToken));

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/members/refresh-token")
			.header("Authorization-refresh", "Bearer " + refreshToken));

		// then
		resultActions
			.andExpect(jsonPath("$.code").value("A-003"));
	}

	@Test
	@DisplayName("로그아웃 후에는 accessToken으로 로그인 불가능하다.")
	public void accessToken으로로그인불가능() throws Exception {
		// given
		mockMvc.perform(post("/api/members/logout")
			.header("Authorization", "Bearer " + accessToken));

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/members/logout")
			.header("Authorization", "Bearer " + accessToken));

		// then
		resultActions
			.andExpect(jsonPath("$.code").value("A-004"));
	}
}
