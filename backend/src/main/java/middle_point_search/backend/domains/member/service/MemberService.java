package middle_point_search.backend.domains.member.service;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.common.util.encoder.PasswordEncoderUtil;
import middle_point_search.backend.domains.email.service.EmailService;
import middle_point_search.backend.domains.email.service.SignupVerificationCodeService;
import middle_point_search.backend.domains.logout.LogoutService;
import middle_point_search.backend.domains.logout.LogoutToken;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.domain.Role;
import middle_point_search.backend.domains.member.dto.MemberDTO.MemberCreateRequest;
import middle_point_search.backend.domains.member.dto.request.SendEmailVerificationRequest;
import middle_point_search.backend.domains.member.dto.request.VerifyEmailVerificationCodeRequest;
import middle_point_search.backend.domains.member.dto.response.VerifyEmailVerificationCodeResponse;
import middle_point_search.backend.domains.member.repository.MemberRepository;
import middle_point_search.backend.domains.refreshToken.RefreshTokenService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoderUtil passwordEncoderUtil;
	private final RefreshTokenService refreshTokenService;
	private final LogoutService logoutService;
	private final SignupVerificationCodeService signupVerificationCodeService;
	private final EmailService emailService;

	// 회원가입하기
	@Transactional
	public void createMember(MemberCreateRequest request) {
		validateExistingEmail(request.getEmail());

		String pw = passwordEncoderUtil.encodePassword(request.getPw());

		Member member = createMemberEntity(request, pw);

		memberRepository.save(member);
	}

	// 주소 여부에 따라 회원 엔티티 생성
	private Member createMemberEntity(MemberCreateRequest request, String pw) {
		if (request.getExistAddress()) {
			return Member.createWithAddress(
				request.getEmail(),
				pw,
				request.getName(),
				Role.USER,
				request.getSiDo(),
				request.getSiGunGu(),
				request.getRoadNameAddress(),
				request.getAddressLatitude(),
				request.getAddressLongitude()
			);
		} else {
			return Member.createWithoutAddress(request.getEmail(), pw, request.getName(), Role.USER);
		}
	}

	// 중복 회원 체크하기
	private void validateExistingEmail(String email) {
		if (memberRepository.existsByEmail(email)) {
			throw CustomException.from(DUPLICATE_MEMBER_EMAIL);
		}
	}

	// 회원 로그아웃 하기
	@Transactional
	public void logoutMember(Long memberId, String accessToken) {
		// 회원의 refreshToken 삭제
		refreshTokenService.deleteByMemberId(memberId);

		// 같은 accessToken으로 다시 로그인하지 못하도록 블랙리스트에 저장
		logoutService.save(new LogoutToken(accessToken));
	}

	// 이메일 중복 체크 및 인증 이메일 보내기
	public void validateDuplicatedEmailAndSendEmailVerification(
		SendEmailVerificationRequest request
	) {
		String email = request.getEmail();

		validateExistingEmail(email);

		String verificationCode = signupVerificationCodeService.createVerificationCode();
		signupVerificationCodeService.checkEmailCodeDuplicationAndSaveEmailCode(email, verificationCode);
		emailService.sendVerificationCodeEmail(email, verificationCode);
	}

	// 인증 코드 인증
	public VerifyEmailVerificationCodeResponse verifyEmailVerificationCode(
		VerifyEmailVerificationCodeRequest request
	) {
		boolean isVerified = signupVerificationCodeService.verifyEmailCode(request.getEmail(), request.getCode());

		return new VerifyEmailVerificationCodeResponse(isVerified);
	}
}

