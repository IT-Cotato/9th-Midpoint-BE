package middle_point_search.backend.domains.member;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import middle_point_search.backend.common.BaseIntegrationTest;
import middle_point_search.backend.common.dto.AccessTokenAndRefreshToken;

@DisplayName("액세스 토큰 재발급 테스트")
public class ReIssueAccessTokenTest extends BaseIntegrationTest {

	private String memberRefreshToken;

	@BeforeEach
	void setUp() throws Exception {
		AccessTokenAndRefreshToken accessTokenAndRefreshToken = signupAndLoginMember(false);

		memberRefreshToken = accessTokenAndRefreshToken.refreshToken();
	}

	@Test
	@DisplayName("엑세스 토큰 재발급에 성공한다.")
	public void 엑세스토큰재발급성공() throws Exception {
		// given
		// when
		ResultActions resultActions1 = mockMvc.perform(post("/api/members/refresh-token")
			.header("Authorization-refresh", "Bearer " + memberRefreshToken));

		// then
		resultActions1
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.accessToken").exists())
			.andExpect(jsonPath("$.data.refreshToken").exists());
	}

	@Test
	@DisplayName("리프레시 토큰이 오래됐을 때 엑세스 토큰 재발급에 실패한다.")
	public void 리프레시토큰이오래됐을때엑세스토큰재발급실패() throws Exception {
		// given
		final String wrongRefreshToken = "wrong";

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/members/refresh-token")
			.header("Authorization-refresh", "Bearer " + wrongRefreshToken));

		// then
		resultActions
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value("A-003"));
	}
}
