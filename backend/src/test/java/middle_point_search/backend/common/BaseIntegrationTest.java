package middle_point_search.backend.common;

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
import middle_point_search.backend.common.dto.AccessTokenAndRefreshToken;
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

	// 주소 없는 회원 생성 및 로그인
	public static final String NO_ADDRESS_MEMBER_EMAIL = "email@test.com";
	public static final String NO_ADDRESS_MEMBER_PW = "1234";
	public static final String NO_ADDRESS_MEMBER_NAME = "name";

	// 주소 있는 회원 생성 및 로그인
	public static final String ADDRESS_MEMBER_EMAIL = "email2@test.com";
	public static final String ADDRESS_MEMBER_PW = "1234";
	public static final String ADDRESS_MEMBER_NAME = "name2";
	public static final String ADDRESS_MEMBER_SI_DO = "서울";
	public static final String ADDRESS_MEMBER_SI_GUN_GU = "강남구";
	public static final String ADDRESS_MEMBER_ROAD_NAME_ADDRESS = "강남대로 123";
	public static final Double ADDRESS_MEMBER_ADDRESS_LATITUDE = 37.123456;
	public static final Double ADDRESS_MEMBER_ADDRESS_LONGITUDE = 127.123456;

	public AccessTokenAndRefreshToken signupAndLoginMember(boolean withAddress) throws Exception {
		// 주소 여부에 따라 Member 엔티티 생성 및 저장
		Member member;
		if (withAddress) {
			member = Member.createWithAddress(
				ADDRESS_MEMBER_EMAIL,
				passwordEncoder.encode(ADDRESS_MEMBER_PW),
				ADDRESS_MEMBER_NAME,
				Role.USER,
				ADDRESS_MEMBER_SI_DO,
				ADDRESS_MEMBER_SI_GUN_GU,
				ADDRESS_MEMBER_ROAD_NAME_ADDRESS,
				ADDRESS_MEMBER_ADDRESS_LATITUDE,
				ADDRESS_MEMBER_ADDRESS_LONGITUDE
			);
		} else {
			member = Member.createWithoutAddress(
				NO_ADDRESS_MEMBER_EMAIL,
				passwordEncoder.encode(NO_ADDRESS_MEMBER_PW),
				NO_ADDRESS_MEMBER_NAME,
				Role.USER
			);
		}
		memberRepository.save(member);

		// 로그인
		LoginMemberRequest loginMemberRequest = new LoginMemberRequest(
			withAddress ? ADDRESS_MEMBER_EMAIL : NO_ADDRESS_MEMBER_EMAIL,
			withAddress ? ADDRESS_MEMBER_PW : NO_ADDRESS_MEMBER_PW
		);
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

	public <T> T getResponseData(ResultActions resultActions, Class<T> responseType) throws Exception {
		String responseContent = resultActions.andReturn().getResponse().getContentAsString();
		JsonNode rootNode = objectMapper.readTree(responseContent);
		JsonNode dataNode = rootNode.path("data");  // "data" 내부만 가져옴
		return objectMapper.treeToValue(dataNode, responseType);
	}
}
