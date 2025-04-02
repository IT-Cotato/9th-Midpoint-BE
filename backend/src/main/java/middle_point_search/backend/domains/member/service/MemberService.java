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
import middle_point_search.backend.domains.member.domain.MemberWithdrawalReason;
import middle_point_search.backend.domains.member.domain.Role;
import middle_point_search.backend.domains.member.dto.request.CreateMemberRequest;
import middle_point_search.backend.domains.member.dto.request.DeleteMemberRequest;
import middle_point_search.backend.domains.member.dto.request.FindMemberInfoResponse;
import middle_point_search.backend.domains.member.dto.request.SendEmailVerificationRequest;
import middle_point_search.backend.domains.member.dto.request.SendNewPasswordRequest;
import middle_point_search.backend.domains.member.dto.request.SendNewPasswordResponse;
import middle_point_search.backend.domains.member.dto.request.SendPasswordReissueVerificationRequest;
import middle_point_search.backend.domains.member.dto.request.UpdateMemberAddressRequest;
import middle_point_search.backend.domains.member.dto.request.UpdateMemberNameRequest;
import middle_point_search.backend.domains.member.dto.request.VerifyEmailVerificationCodeRequest;
import middle_point_search.backend.domains.member.dto.response.FindProfileImageUrlResponse;
import middle_point_search.backend.domains.member.dto.response.VerifyEmailVerificationCodeResponse;
import middle_point_search.backend.domains.member.repository.MemberRepository;
import middle_point_search.backend.domains.member.repository.MemberWithdrawalReasonRepository;
import middle_point_search.backend.domains.memberRoom.repository.MemberRoomRepository;
import middle_point_search.backend.domains.place.repository.PlaceRepository;
import middle_point_search.backend.domains.placeVoteRoom.repository.PlaceVoteRepository;
import middle_point_search.backend.domains.refreshToken.service.RefreshTokenService;
import middle_point_search.backend.domains.s3.S3Service;
import middle_point_search.backend.domains.s3.dto.response.CreatePreSignedUrlResponse;
import middle_point_search.backend.domains.s3.model.PreSignedUrlPrefix;
import middle_point_search.backend.domains.timeVoteRoom.repository.TimeVoteRepository;

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
	private final S3Service s3Service;
	private final MemberRoomRepository memberRoomRepository;
	private final PlaceRepository placeRepository;
	private final PlaceVoteRepository placeVoteRepository;
	private final TimeVoteRepository timeVoteRepository;
	private final MemberWithdrawalReasonRepository memberWithdrawalReasonRepository;

	// 회원가입하기
	@Transactional
	public void createMember(CreateMemberRequest request) {
		validateExistingEmail(request.email());
		signupVerificationCodeService.validateEmailCodeAndDelete(request.email(), request.code());

		Member member = createMemberEntity(request, passwordEncoder.encode(request.pw()));

		memberRepository.save(member);
	}

	// 주소 여부에 따라 회원 엔티티 생성
	private Member createMemberEntity(CreateMemberRequest request, String pw) {
		if (request.existAddress()) {
			return Member.createWithAddress(
				request.email(),
				pw,
				request.name(),
				Role.USER,
				request.siDo(),
				request.siGunGu(),
				request.roadNameAddress(),
				request.addressLatitude(),
				request.addressLongitude()
			);
		} else {
			return Member.createWithoutAddress(request.email(), pw, request.name(), Role.USER);
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
	public SendNewPasswordResponse validateCodeAndSendNewPassword(SendNewPasswordRequest request) {
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

		return new SendNewPasswordResponse(newPassword);
	}

	// 새 비밀번호 생성
	private String createNewPassword() {
		// UUID 생성, 6자리
		return UUID.randomUUID()
			.toString()
			.replace("-", "")
			.substring(0, 6);
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

	// 이름 수정
	@Transactional
	public void updateMemberName(Long memberId, UpdateMemberNameRequest request) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> CustomException.from(MEMBER_NOT_FOUND));

		member.updateName(request.name());
	}

	// 주소 수정
	@Transactional
	public void updateMemberAddress(Long memberId, UpdateMemberAddressRequest request) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> CustomException.from(MEMBER_NOT_FOUND));

		member.updateAddress(
			request.siDo(),
			request.siGunGu(),
			request.roadNameAddress(),
			request.addressLatitude(),
			request.addressLongitude());
	}

	// 회원 주소 삭제
	@Transactional
	public void deleteMemberAddress(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> CustomException.from(MEMBER_NOT_FOUND));

		member.deleteAddress();
	}

	// 회원 프로필 presigned path 생성
	@Transactional
	public CreatePreSignedUrlResponse createProfilePreSignedUrl(Long memberId, String filename) {
		CreatePreSignedUrlResponse response = s3Service.createPreSignedUrl(PreSignedUrlPrefix.PROFILE, filename);

		// DB에 path 저장
		memberRepository.findById(memberId)
			.orElseThrow(() -> CustomException.from(MEMBER_NOT_FOUND))
			.updateProfileImagePath(response.path());

		return response;
	}

	// 회원 프로필 이미지 path 조회
	@Transactional
	public FindProfileImageUrlResponse findProfileImageUrl(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> CustomException.from(MEMBER_NOT_FOUND));

		// 1. path가 null이면 false
		// 2. path가 유효하지 않으면 false
		// 3. path가 유효하면 true 및 url 반환
		if (member.getProfileImagePath() == null) {
			return new FindProfileImageUrlResponse(false, null);
		} else if (!s3Service.isFileExists(member.getProfileImagePath())) {
			member.updateProfileImagePath(null);
			return new FindProfileImageUrlResponse(false, null);
		} else {
			return new FindProfileImageUrlResponse(true, s3Service.getUrl(member.getProfileImagePath()));
		}
	}

	// 회원 프로필 이미지 삭제
	@Transactional
	public void deleteProfileImage(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> CustomException.from(MEMBER_NOT_FOUND));

		// path가 null이면 return
		if (member.getProfileImagePath() == null) {
			return;
		}

		s3Service.deleteFile(member.getProfileImagePath());
		member.updateProfileImagePath(null);
	}

	// 회원 삭제
	@Transactional
	public void deleteMember(Long memberId, DeleteMemberRequest request, String accessToken) {
		memberRoomRepository.deleteAllByMemberId(memberId);
		placeVoteRepository.deleteAllByMemberId(memberId);
		timeVoteRepository.deleteAllByMemberId(memberId);
		placeRepository.deleteAllByMemberId(memberId);
		memberRepository.deleteById(memberId);

		// 같은 accessToken 및 refreshToken으로 접속 못하도록 로그아웃
		logoutMember(memberId, accessToken);

		// 회원탈퇴 사유 저장
		memberWithdrawalReasonRepository.save(new MemberWithdrawalReason(request.withdrawalReason()));
	}

	public FindMemberInfoResponse findMemberInfo(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> CustomException.from(MEMBER_NOT_FOUND));

		return FindMemberInfoResponse.from(member);
	}
}

