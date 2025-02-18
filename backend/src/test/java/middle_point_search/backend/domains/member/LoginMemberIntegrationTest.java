package middle_point_search.backend.domains.member;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.JsonNode;

import middle_point_search.backend.common.BaseIntegrationTest;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.domain.Role;
import middle_point_search.backend.domains.member.dto.request.LoginMemberRequest;
import middle_point_search.backend.domains.member.repository.MemberRepository;

@DisplayName("로그인")
public class LoginMemberIntegrationTest extends BaseIntegrationTest {

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private MemberRepository memberRepository;

	private final String email = "email@test.com";
	private final String pw = "1234";

	@BeforeEach
	public void setUp() {
		// 멤버 저장
		Member member = Member.createWithoutAddress(
			email,
			passwordEncoder.encode(pw),
			"name",
			Role.USER
		);

		memberRepository.save(member);
	}

	@Test
	@DisplayName("로그인에 성공한다.")
	public void 로그인성공() throws Exception {
		// given
		// 로그인 요청
		LoginMemberRequest loginMemberRequest = new LoginMemberRequest(email, pw);

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/members/login")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.param("email", loginMemberRequest.email())
			.param("pw", loginMemberRequest.pw())
			.accept(MediaType.APPLICATION_JSON));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.accessToken").exists())
			.andExpect(jsonPath("$.data.refreshToken").exists());
	}

	@Test
	@DisplayName("로그인에 실패한다.")
	public void 로그인실패() throws Exception {
		// given
		// 로그인 요청
		LoginMemberRequest loginMemberRequest = new LoginMemberRequest(email, "differentPw");

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/members/login")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.param("email", loginMemberRequest.email())
			.param("pw", loginMemberRequest.pw()) // 잘못된 비밀번호
			.accept(MediaType.APPLICATION_JSON));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.accessToken").doesNotExist())
			.andExpect(jsonPath("$.data.refreshToken").doesNotExist());
	}

	@Test
	@DisplayName("엑세스 토큰 재발급에 성공한다.")
	public void 엑세스토큰재발급성공() throws Exception {
		// 로그인
		LoginMemberRequest loginMemberRequest = new LoginMemberRequest(email, pw);
		ResultActions resultActions = mockMvc.perform(post("/api/members/login")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.param("email", loginMemberRequest.email())
			.param("pw", loginMemberRequest.pw())
			.accept(MediaType.APPLICATION_JSON));

		String responseContent = resultActions.andReturn().getResponse().getContentAsString();
		JsonNode jsonNode = objectMapper.readTree(responseContent);
		String accessToken = jsonNode.get("data").get("accessToken").asText();
		String refreshToken = jsonNode.get("data").get("refreshToken").asText();

		// when
		ResultActions resultActions1 = mockMvc.perform(post("/api/members/refresh-token")
			.header("Authorization-refresh", "Bearer " + refreshToken));

		// then
		resultActions1
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.accessToken").exists())
			.andExpect(jsonPath("$.data.refreshToken").exists());
	}
}
