package middle_point_search.backend.domains.member;

import static org.junit.jupiter.api.Assertions.*;
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
import middle_point_search.backend.domains.email.domain.SignupVerificationCode;
import middle_point_search.backend.domains.email.repository.SignupVerificationCodeRepository;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.domain.Role;
import middle_point_search.backend.domains.member.dto.request.CreateMemberRequest;
import middle_point_search.backend.domains.member.dto.request.LoginMemberRequest;
import middle_point_search.backend.domains.member.repository.MemberRepository;

public class MemberIntegrationTest extends BaseIntegrationTest {

	@Autowired
	private SignupVerificationCodeRepository signupVerificationCodeRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Nested
	@DisplayName("회원가입")
	class 회원가입테스트 {
		@Test
		@DisplayName("저장된 인증코드를 통해 회원가입 한다.")
		public void 멤버생성() throws Exception {
			// given
			// 인증 코드 저장
			signupVerificationCodeRepository.save(new SignupVerificationCode("email@test.com", "code"));

			CreateMemberRequest createMemberRequest = new CreateMemberRequest(
				"name",
				"email@test.com",
				"pw",
				true,
				"siDo",
				"siGunGu",
				"roadNameAddress",
				1.0,
				1.0,
				"code"
			);
			// when
			ResultActions resultActions = mockMvc.perform(post("/api/members")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createMemberRequest))
				.accept(MediaType.APPLICATION_JSON));
			Member member = memberRepository.findByEmail("email@test.com").orElseThrow();

			// then
			// 회원가입 성공
			resultActions.andExpect(status().isOk());

			// 멤버 생성 확인
			assertEquals(member.getEmail(), createMemberRequest.email());
			assertEquals(member.getName(), createMemberRequest.name());
			assertTrue(passwordEncoder.matches(createMemberRequest.pw(), member.getPw()));
			assertEquals(member.getRole(), Role.USER);
			assertEquals(member.getSiDo(), createMemberRequest.siDo());
			assertEquals(member.getSiGunGu(), createMemberRequest.siGunGu());
			assertEquals(member.getRoadNameAddress(), createMemberRequest.roadNameAddress());
			assertEquals(member.getAddressLatitude(), createMemberRequest.addressLatitude());
			assertEquals(member.getAddressLongitude(), createMemberRequest.addressLongitude());
		}

		@Test
		@DisplayName("저장된 인증코드를 통해 회원가입 한다.")
		public void 멤버저장() throws Exception {
			// given
			// 인증 코드 저장
			signupVerificationCodeRepository.save(new SignupVerificationCode("email@test.com", "code"));

			CreateMemberRequest createMemberRequest = new CreateMemberRequest(
				"name",
				"email@test.com",
				"pw",
				true,
				"siDo",
				"siGunGu",
				"roadNameAddress",
				1.0,
				1.0,
				"code"
			);
			// when
			mockMvc.perform(post("/api/members")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createMemberRequest))
				.accept(MediaType.APPLICATION_JSON));
			Member member = memberRepository.findByEmail("email@test.com").orElseThrow();

			// then
			// 멤버 저장 확인
			assertEquals(member.getEmail(), createMemberRequest.email());
			assertEquals(member.getName(), createMemberRequest.name());
			assertTrue(passwordEncoder.matches(createMemberRequest.pw(), member.getPw()));
			assertEquals(member.getRole(), Role.USER);
			assertEquals(member.getSiDo(), createMemberRequest.siDo());
			assertEquals(member.getSiGunGu(), createMemberRequest.siGunGu());
			assertEquals(member.getRoadNameAddress(), createMemberRequest.roadNameAddress());
			assertEquals(member.getAddressLatitude(), createMemberRequest.addressLatitude());
			assertEquals(member.getAddressLongitude(), createMemberRequest.addressLongitude());
		}

		@ParameterizedTest
		@MethodSource("provideMemberRequests")
		@DisplayName("요청 형식이 알맞지 않으면 회원가입에 실패한다.")
		public void 요청형식알맞지않은경우_회원가입실패(CreateMemberRequest createMemberRequest) throws Exception {

			// when
			ResultActions resultActions = mockMvc.perform(post("/api/members")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createMemberRequest))
				.accept(MediaType.APPLICATION_JSON));
			// then
			resultActions
				.andExpect(status().isBadRequest());
		}

		// 여러 CreateMemberRequest 객체를 생성하여 반환
		private static Stream<CreateMemberRequest> provideMemberRequests() {
			return Stream.of(
				// 이름이 없는 경우
				new CreateMemberRequest("", "email1@test.com", "pw1", true, "siDo", "siGunGu", "roadName", 1.0, 1.0,
					"code"),
				// 이름이 너무 긴 경우
				new CreateMemberRequest("n".repeat(31), "email1@test.com", "pw1", true, "siDo", "siGunGu", "roadName",
					1.0, 1.0, "code"),
				// 이름이 너무 짧은 경우
				new CreateMemberRequest("n", "email1@test.com", "pw1", true, "siDo", "siGunGu", "roadName", 1.0, 1.0,
					"code"),
				// 이메일이 없는 경우
				new CreateMemberRequest("name1", "", "pw1", true, "siDo", "siGunGu", "roadName", 1.0, 1.0, "code"),
				// 이메일이 너무 긴 경우
				new CreateMemberRequest("name1", "e".repeat(255) + "@test.com", "pw1", true, "siDo", "siGunGu",
					"roadName", 1.0, 1.0, "code"),
				// 이메일 형식이 아닌 경우
				new CreateMemberRequest("name1", "email", "pw1", true, "siDo", "siGunGu", "roadName", 1.0, 1.0, "code"),
				// 비밀번호가 없는 경우
				new CreateMemberRequest("name1", "email1@test.com", "", true, "siDo", "siGunGu", "roadName", 1.0, 1.0,
					"code"),
				// 비밀번호가 너무 긴 경우
				new CreateMemberRequest("name1", "email1@test.com", "p".repeat(21), true, "siDo", "siGunGu", "roadName",
					1.0, 1.0, "code"),
				// 인증코드가 없는 경우
				new CreateMemberRequest("name1", "email1@test.com", "pw1", true, "siDo", "siGunGu", "roadName", 1.0,
					1.0, "")
			);
		}

		@Test
		@DisplayName("이메일이 중복되면 회원가입에 실패한다.")
		public void 이메일중복_회원가입실패() throws Exception {
			// given
			// 멤버 저장
			Member member = Member.createWithoutAddress(
				"email@test.com",
				"pw",
				"name",
				Role.USER
			);
			memberRepository.save(member);

			// 인증 코드 저장
			signupVerificationCodeRepository.save(new SignupVerificationCode("email@test.com", "code"));

			CreateMemberRequest createMemberRequest = new CreateMemberRequest(
				"name",
				"email@test.com",
				"pw",
				true,
				"siDo",
				"siGunGu",
				"roadNameAddress",
				1.0,
				1.0,
				"code"
			);

			// when
			ResultActions resultActions = mockMvc.perform(post("/api/members")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createMemberRequest))
				.accept(MediaType.APPLICATION_JSON));
			// then
			resultActions
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value("M-001"));
		}
	}

	@Nested
	@DisplayName("로그인")
	class 로그인테스트 {

		@BeforeEach
		public void setUp() {
			// 멤버 저장
			Member member = Member.createWithoutAddress(
				"email@test.com",
				passwordEncoder.encode("1234"),
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
			LoginMemberRequest loginMemberRequest = new LoginMemberRequest("email@test.com", "1234");

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
			LoginMemberRequest loginMemberRequest = new LoginMemberRequest("email@test.com", "differentPw");

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
	}
}