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

import middle_point_search.backend.common.BaseIntegrationTest;
import middle_point_search.backend.common.dto.AccessTokenAndRefreshToken;
import middle_point_search.backend.domains.member.dto.request.UpdateMemberAddressRequest;
import middle_point_search.backend.domains.member.dto.request.UpdateMemberNameRequest;
import middle_point_search.backend.domains.member.dto.request.UpdatePasswordRequest;
import middle_point_search.backend.domains.member.repository.MemberRepository;

@DisplayName("회원 정보 수정")
public class MemberInfoIntegrationTest extends BaseIntegrationTest {

	private String accessTokenFromNoAddressMember;
	private String accessTokenFromAddressMember;

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private MemberRepository memberRepository;

	@BeforeEach
	public void setUp() throws Exception {
		AccessTokenAndRefreshToken accessTokenAndRefreshToken = signupAndLoginMember(false);
		accessTokenFromNoAddressMember = accessTokenAndRefreshToken.accessToken();

		accessTokenAndRefreshToken = signupAndLoginMember(true);
		accessTokenFromAddressMember = accessTokenAndRefreshToken.accessToken();
	}

	@Nested
	@DisplayName("비밀번호 변경")
	class 비밀번호_변경 {
		@Test
		@DisplayName("비밀번호 수정에 성공한다.")
		public void 비밀번호수정성공() throws Exception {
			// given
			String newPw = "5678";

			UpdatePasswordRequest request = new UpdatePasswordRequest(NO_ADDRESS_MEMBER_PW, newPw);

			// when
			mockMvc.perform(patch("/api/members/password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header("Authorization", "Bearer " + accessTokenFromNoAddressMember)
				.accept(MediaType.APPLICATION_JSON)
			);

			// then
			// 변경된 비밀번호가 저장되었는지
			memberRepository.findByEmail(NO_ADDRESS_MEMBER_EMAIL).ifPresent(member -> {
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
				.header("Authorization", "Bearer " + accessTokenFromNoAddressMember)
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
				.header("Authorization", "Bearer " + accessTokenFromNoAddressMember)
				.accept(MediaType.APPLICATION_JSON)
			);

			// then
			resultActions.andExpect(jsonPath("$.code").value("C-202"));
		}

		private static Stream<UpdatePasswordRequest> provideInvalidUpdatePasswordRequest() {
			return Stream.of(
				new UpdatePasswordRequest(NO_ADDRESS_MEMBER_PW, null),
				new UpdatePasswordRequest(NO_ADDRESS_MEMBER_PW, ""),
				new UpdatePasswordRequest(NO_ADDRESS_MEMBER_PW, "012345678901234567891")
			);
		}
	}

	@Nested
	@DisplayName("회원 정보 조회")
	class 회원_정보_조회 {
		@Test
		@DisplayName("회원 정보 조회에 성공한다.")
		public void 회원정보조회성공() throws Exception {
			// when
			ResultActions resultActions = mockMvc.perform(get("/api/members/info")
				.header("Authorization", "Bearer " + accessTokenFromNoAddressMember)
				.accept(MediaType.APPLICATION_JSON)
			);

			// then
			resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.email").value(NO_ADDRESS_MEMBER_EMAIL))
				.andExpect(jsonPath("$.data.name").value(NO_ADDRESS_MEMBER_NAME))
				.andExpect(jsonPath("$.data.existAddress").value("false"));
		}

		@Test
		@DisplayName("주소 있는 회원 정보 조회에 성공한다.")
		public void 주소있는회원정보조회성공() throws Exception {
			// when
			ResultActions resultActions = mockMvc.perform(get("/api/members/info")
				.header("Authorization", "Bearer " + accessTokenFromAddressMember)
				.accept(MediaType.APPLICATION_JSON)
			);

			// then
			resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.email").value(ADDRESS_MEMBER_EMAIL))
				.andExpect(jsonPath("$.data.name").value(ADDRESS_MEMBER_NAME))
				.andExpect(jsonPath("$.data.existAddress").value("true"))
				.andExpect(jsonPath("$.data.siDo").value(ADDRESS_MEMBER_SI_DO))
				.andExpect(jsonPath("$.data.siGunGu").value(ADDRESS_MEMBER_SI_GUN_GU))
				.andExpect(jsonPath("$.data.roadNameAddress").value(ADDRESS_MEMBER_ROAD_NAME_ADDRESS))
				.andExpect(jsonPath("$.data.addressLatitude").value(ADDRESS_MEMBER_ADDRESS_LATITUDE))
				.andExpect(jsonPath("$.data.addressLongitude").value(ADDRESS_MEMBER_ADDRESS_LONGITUDE));
		}
	}

	@Nested
	@DisplayName("닉네임 수정")
	class 닉네임_수정 {
		@Test
		@DisplayName("닉네임 수정에 성공한다.")
		public void 닉네임수정성공() throws Exception {
			// given
			String newName = "newName";

			UpdateMemberNameRequest request = new UpdateMemberNameRequest(newName);

			// when
			mockMvc.perform(patch("/api/members/name")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header("Authorization", "Bearer " + accessTokenFromNoAddressMember)
				.accept(MediaType.APPLICATION_JSON)
			);

			// then
			// 변경된 닉네임이 저장되었는지
			memberRepository.findByEmail(NO_ADDRESS_MEMBER_EMAIL).ifPresent(member -> {
				assertThat(member.getName()).isEqualTo(newName);
			});
		}

		@ParameterizedTest
		@MethodSource("provideInvalidName")
		@DisplayName("닉네임이 형식이 맞지 않으면 닉네임 수정에 실패한다.")
		public void 닉네임형식오류_닉네임수정실패(String newName) throws Exception {
			// given
			UpdateMemberNameRequest request = new UpdateMemberNameRequest(newName);

			// when
			ResultActions resultActions = mockMvc.perform(patch("/api/members/name")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header("Authorization", "Bearer " + accessTokenFromNoAddressMember)
				.accept(MediaType.APPLICATION_JSON)
			);

			// then
			resultActions.andExpect(jsonPath("$.code").value("C-202"));
		}

		private static Stream<String> provideInvalidName() {
			return Stream.of(
				null,
				"",
				"0123456789012345678901234567890123" // 30자 이상
			);
		}
	}

	@Nested
	@DisplayName("주소 수정")
	class 주소_수정 {
		@Test
		@DisplayName("주소 수정에 성공한다.")
		public void 주소수정성공() throws Exception {
			// given
			String siDo = "서울특별시";
			String siGunGu = "강남구";
			String roadNameAddress = "강남대로 123";
			Double addressLatitude = 37.123456;
			Double addressLongitude = 127.123456;

			// when
			mockMvc.perform(patch("/api/members/address")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new UpdateMemberAddressRequest(
					siDo,
					siGunGu,
					roadNameAddress,
					addressLatitude,
					addressLongitude
				)))
				.header("Authorization", "Bearer " + accessTokenFromNoAddressMember)
				.accept(MediaType.APPLICATION_JSON)
			);

			// then
			// 변경된 주소가 저장되었는지
			memberRepository.findByEmail(NO_ADDRESS_MEMBER_EMAIL).ifPresent(member -> {
				assertThat(member.getExistAddress()).isTrue(); // false에서 true로 변경되었는지
				assertThat(member.getSiDo()).isEqualTo(siDo);
				assertThat(member.getSiGunGu()).isEqualTo(siGunGu);
				assertThat(member.getRoadNameAddress()).isEqualTo(roadNameAddress);
				assertThat(member.getAddressLatitude()).isEqualTo(addressLatitude);
				assertThat(member.getAddressLongitude()).isEqualTo(addressLongitude);
			});
		}
	}

	@Nested
	@DisplayName("주소 있는 회원 주소 삭제")
	class 주소_있는_회원_주소_삭제 {
		@Test
		@DisplayName("주소 삭제에 성공한다.")
		public void 주소삭제성공() throws Exception {
			// when
			mockMvc.perform(delete("/api/members/address")
				.header("Authorization", "Bearer " + accessTokenFromAddressMember)
				.accept(MediaType.APPLICATION_JSON)
			);

			// then
			// 주소가 삭제되었는지
			memberRepository.findByEmail(ADDRESS_MEMBER_EMAIL).ifPresent(member -> {
				assertThat(member.getExistAddress()).isFalse();
			});
		}
	}


}
