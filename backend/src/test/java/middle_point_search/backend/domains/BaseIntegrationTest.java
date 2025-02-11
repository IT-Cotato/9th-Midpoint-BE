package middle_point_search.backend.domains;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.domain.Role;
import middle_point_search.backend.domains.member.dto.AccessTokenAndRefreshToken;
import middle_point_search.backend.domains.member.dto.request.LoginMemberRequest;
import middle_point_search.backend.domains.member.repository.MemberRepository;

@SpringBootTest
@Disabled
@AutoConfigureMockMvc
@Transactional
public class BaseIntegrationTest {
	@Autowired
	protected MockMvc mockMvc;
	@Autowired
	protected ObjectMapper objectMapper;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private MemberRepository memberRepository;

	public final String MEMBER_EMAIL = "email@test.com";
	public final String MEMBER_PW = "1234";

	public AccessTokenAndRefreshToken signupAndLogin() throws Exception {

		// 멤버 저장
		Member member = Member.createWithoutAddress(
			MEMBER_EMAIL,
			passwordEncoder.encode(MEMBER_PW),
			"name",
			Role.USER
		);

		memberRepository.save(member);

		// 로그인
		LoginMemberRequest loginMemberRequest = new LoginMemberRequest(MEMBER_EMAIL, MEMBER_PW);
		ResultActions resultActions = mockMvc.perform(post("/api/members/login")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.param("email", loginMemberRequest.email())
			.param("pw", loginMemberRequest.pw())
			.accept(MediaType.APPLICATION_JSON));

		String responseContent = resultActions.andReturn().getResponse().getContentAsString();
		JsonNode jsonNode = objectMapper.readTree(responseContent);
		String accessToken = jsonNode.get("data").get("accessToken").asText();
		String refreshToken = jsonNode.get("data").get("refreshToken").asText();

		return new AccessTokenAndRefreshToken(accessToken, refreshToken);
	}
}
