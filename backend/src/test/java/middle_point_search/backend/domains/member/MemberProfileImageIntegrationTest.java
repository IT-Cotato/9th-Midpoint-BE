package middle_point_search.backend.domains.member;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import middle_point_search.backend.common.BaseIntegrationTest;
import middle_point_search.backend.common.dto.AccessTokenAndRefreshToken;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.repository.MemberRepository;
import middle_point_search.backend.domains.member.service.MemberService;

@DisplayName("회원 프로필 이미지")
public class MemberProfileImageIntegrationTest extends BaseIntegrationTest {

	String accessToken;
	String memberEmail;

	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private MemberService memberService;

	@BeforeEach
	public void setUp() throws Exception {
		// 회원가입 및 로그인
		AccessTokenAndRefreshToken accessTokenAndRefreshToken = signupAndLoginMember(false);
		accessToken = accessTokenAndRefreshToken.accessToken();
		memberEmail = NO_ADDRESS_MEMBER_EMAIL;
	}

	@Test
	@DisplayName("프로필 이미지 presigned url을 생성 및 저장")
	public void 프로필이미지_presignedurl_생성_및_저장() throws Exception {
		// given
		String fileName = "profile.jpg";
		Member member = memberRepository.findByEmail(memberEmail).get();

		// when
		ResultActions resultActions = mockMvc.perform(get("/api/members/profile/presigned")
			.header("Authorization", "Bearer " + accessToken)
			.param("filename", fileName));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.preSignedUrl").exists())
			.andExpect(jsonPath("$.data.path").value(member.getProfileImagePath()));
	}

	@Test
	@DisplayName("presigned url을 발급했지만, 저장하지 않았으면 url을 조회하지 못한다.")
	public void 프로필이미지_미저장_조회() throws Exception {
		// given
		String fileName = "profile.jpg";
		Member member = memberRepository.findByEmail(memberEmail).get();
		// 프로필 이미지 저장
		memberService.createProfilePreSignedUrl(member.getId(), fileName);
		// 프론트엔드가 저장하지 않아, s3에는 저장되지 않음.

		// when
		ResultActions resultActions = mockMvc.perform(get("/api/members/profile")
			.header("Authorization", "Bearer " + accessToken));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.isExist").value(false));
	}

	@Test
	@DisplayName("프로필 이미지 삭제")
	public void 프로필이미지_삭제() throws Exception {
		// given
		String fileName = "profile.jpg";
		Member member = memberRepository.findByEmail(memberEmail).get();
		// 프로필 이미지 저장
		memberService.createProfilePreSignedUrl(member.getId(), fileName);
		// 프론트엔드가 저장하지 않아, s3에는 저장되지 않음.

		// when
		ResultActions resultActions = mockMvc.perform(delete("/api/members/profile")
			.header("Authorization", "Bearer " + accessToken));

		// then
		resultActions
			.andExpect(status().isOk());

		Assertions.assertEquals(null, memberRepository.findByEmail(memberEmail).get().getProfileImagePath());
	}

	// s3 저장 테스트 미흡
}
