package middle_point_search.backend.domains.member.service;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.domains.email.service.EmailService;
import middle_point_search.backend.domains.email.service.PasswordReissueVerificationCodeService;
import middle_point_search.backend.domains.email.service.SignupVerificationCodeService;
import middle_point_search.backend.domains.logout.LogoutService;
import middle_point_search.backend.domains.logout.LogoutToken;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.domain.Role;
import middle_point_search.backend.domains.member.dto.MemberDTO.MemberCreateRequest;
import middle_point_search.backend.domains.member.dto.request.SendEmailVerificationRequest;
import middle_point_search.backend.domains.member.dto.request.SendNewPasswordRequest;
import middle_point_search.backend.domains.member.dto.request.SendPasswordReissueVerificationRequest;
import middle_point_search.backend.domains.member.dto.request.UpdateMemberInfoRequest;
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
	private final PasswordEncoder passwordEncoder;
	private final RefreshTokenService refreshTokenService;
	private final LogoutService logoutService;
	private final SignupVerificationCodeService signupVerificationCodeService;
	private final EmailService emailService;
	private final PasswordReissueVerificationCodeService passwordReissueVerificationCodeService;

	// 회원가입하기
	@Transactional
	public void createMember(MemberCreateRequest request) {
		validateExistingEmail(request.getEmail());
		signupVerificationCodeService.validateEmailCodeAndDelete(request.getEmail(), request.getCode());

		String pw = passwordEncoder.encode(request.getPw());

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

	// 비밀번호 변경
	@Transactional
	public void updatePassword(Long memberId, String password, String newPassword) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> CustomException.from(MEMBER_NOT_FOUND));

		checkPassword(password, member.getPw());

		member.updatePassword(passwordEncoder.encode(newPassword));
	}

	// 비밀번호 일치 확인
	private void checkPassword(String password, String encodedPassword) {
		boolean matches = passwordEncoder.matches(password, encodedPassword);

		if (!matches) {
			throw CustomException.from(PASSWORD_NOT_MATCH);
		}
	}

	// 비밀번호 재발급
	@Transactional
	public void validateCodeAndSendNewPassword(SendNewPasswordRequest request) {
		String email = request.getEmail();

		// 토큰 검증 및 삭제
		passwordReissueVerificationCodeService.verifyEmailCode(email, request.getCode());

		// 맞는 게 있다면 그 member 비밀번호 변경 및 전송
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> CustomException.from(MEMBER_NOT_FOUND));

		// 비밀번호 변경
		String newPassword = createNewPassword();
		member.updatePassword(passwordEncoder.encode(newPassword));

		// 새 비밀번호 이메일 전송
		emailService.sendNewPassword(email, newPassword);
	}

	// 새 비밀번호 생성
	private String createNewPassword() {
		// UUID 생성
		String uuid = UUID.randomUUID().toString().replace("-", "");

		// 첫 6자 추출
		return uuid.substring(0, 6);
	}

	// 비밀번호 재발급 인증 코드 보내기
	public void sendPasswordReissueVerification(SendPasswordReissueVerificationRequest request) {
		// 존재하는 회원이 아니면 에러
		if (!memberRepository.existsByEmail(request.getEmail())) {
			throw CustomException.from(MEMBER_NOT_FOUND);
		}

		String code = passwordReissueVerificationCodeService.createVerificationCode();
		passwordReissueVerificationCodeService.checkEmailCodeDuplicationAndSaveEmailCode(request.getEmail(), code);
		emailService.sendPasswordReissueVerificationCodeEmail(request.getEmail(), code);
	}

	// 회원 정보(닉네임, 주소) 수정
	@Transactional
	public void updateMemberInfo(Long memberId, UpdateMemberInfoRequest request) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> CustomException.from(MEMBER_NOT_FOUND));

		member.updateName(request.name());
		member.updateAddress(
			request.siDo(),
			request.siGunGu(),
			request.roadNameAddress(),
			request.addressLatitude(),
			request.addressLongitude());
	}
}

