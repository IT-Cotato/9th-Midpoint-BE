package middle_point_search.backend.domains.member.service;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.common.s3.AwsS3Service;
import middle_point_search.backend.common.util.encoder.PasswordEncoderUtil;
import middle_point_search.backend.domains.logout.LogoutService;
import middle_point_search.backend.domains.logout.LogoutToken;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.domain.Role;
import middle_point_search.backend.domains.member.dto.MemberDTO.MemberCreateRequest;
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
	private final AwsS3Service awsS3Service;

	@Value("${default.profile.image.url}") // application.properties 또는 application.yml에서 기본 이미지 URL 설정
	private String defaultProfileImageUrl;

	// 회원가입하기
	@Transactional
	public void createMember(MemberCreateRequest request, MultipartFile profileImageFile) {
		String pw = passwordEncoderUtil.encodePassword(request.getPw());
		String directoryPath = "profile/default/";
		String profileImageUrl;

		// 파일이 존재하는 경우 업로드, 없는 경우 기본 URL 사용
		try {
			// 파일이 있는 경우 업로드, 없는 경우 기본 URL 사용
			if (profileImageFile != null && !profileImageFile.isEmpty()) {
				profileImageUrl = awsS3Service.uploadFile(profileImageFile, directoryPath);
			} else {
				profileImageUrl = defaultProfileImageUrl;
			}
		} catch (Exception e) {
			throw CustomException.from(MEMBER_FILE_UPLOAD_ERROR);
		}
		Member member = Member.from(request.getEmail(), pw, request.getName(), Role.USER, profileImageUrl);
		memberRepository.save(member);
	}

	// 회원 로그아웃 하기
	@Transactional
	public void logoutMember(Member member, String accessToken) {
		Long memberId = member.getId();

		// 회원의 refreshToken 삭제
		refreshTokenService.deleteByMemberId(memberId);

		// 같은 accessToken으로 다시 로그인하지 못하도록 블랙리스트에 저장
		logoutService.save(new LogoutToken(accessToken));
	}

	@Transactional
	public String updateProfileImage(Long memberId, MultipartFile profileImageFile) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> CustomException.from(MEMBER_NOT_FOUND));

		// S3에 업로드할 경로 설정
		String directoryPath = "profile/" + memberId + "/";

		// S3에 파일 업로드
		String profileImageUrl = awsS3Service.uploadFile(profileImageFile, directoryPath);

		// 엔티티 업데이트
		member.updateProfileImageUrl(profileImageUrl);

		return profileImageUrl;
	}
}
