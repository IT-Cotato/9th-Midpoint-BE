package middle_point_search.backend.domains.member.controller;

import static middle_point_search.backend.domains.member.dto.MemberDTO.*;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.dto.DataResponse;
import middle_point_search.backend.common.dto.ErrorResponse;
import middle_point_search.backend.common.security.filter.jwtFilter.JwtTokenProvider;
import middle_point_search.backend.common.util.MemberLoader;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.service.MemberService;

@Tag(name = "MEMBER API", description = "회원에 대한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

	private final MemberService memberService;
	private final MemberLoader memberLoader;
	private final JwtTokenProvider jwtTokenProvider;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(
		summary = "회원가입",
		description = """
			회원가입한다.
						
			이름, 이메일, 비밀번호를 입력받아 회원가입한다.""",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
			),
			@ApiResponse(
				responseCode = "400",
				description = "요청 파라미터가 잘못되었습니다.[C-202]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
		}
	)
	public ResponseEntity<DataResponse<Void>> memberCreate(
		@RequestPart @Valid MemberCreateRequest request,
		@RequestPart(value = "profileImageFile", required = false)
		MultipartFile profileImageFile) {
		memberService.createMember(request, profileImageFile);

		return ResponseEntity.ok(DataResponse.ok());
	}

	@PostMapping("/logout")
	@Operation(
		summary = "로그아웃",
		description = """
			로그아웃한다.
						
			AccessToken 필요.""",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
			),
			@ApiResponse(
				responseCode = "401",
				description = "인증에 실패하였습니다.[C-101]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "402",
				description = "Access Token을 재발급해야합니다.[A-004]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
		}
	)
	public ResponseEntity<DataResponse<Void>> memberLogout(HttpServletRequest request) {
		String accessToken = jwtTokenProvider.extractAccessToken(request).orElse(null);
		Member member = memberLoader.getMember();

		memberService.logoutMember(member, accessToken);

		return ResponseEntity.ok(DataResponse.ok());
	}

	@PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	@Operation(
		summary = "로그인",
		description = "로그인 성공 시 accessToken, refreshToken을 반환",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
			),
			@ApiResponse(
				responseCode = "200",
				description = "로그인 성공, 로그인 실패(보안을 위해 비밀번호가 틀렸어도 200리턴"
			)
		}
	)
	public ResponseEntity<DataResponse<Void>> loginMember(
		@RequestParam("email") String email,
		@RequestParam("pw") String pw
	) {
		// 이 메소드는 실제로 실행되지 않습니다. 문서용도로만 사용됩니다.
		return ResponseEntity.ok(DataResponse.ok());
	}

	@PostMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(
		summary = "프로필 이미지 업로드",
		description = """
			인증된 사용자의 프로필 이미지를 업로드한다.
			            
			성공 시 업로드된 이미지의 URL을 반환합니다.""",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공",
				content = @Content(schema = @Schema(implementation = DataResponse.class))
			),
			@ApiResponse(
				responseCode = "400",
				description = "잘못된 요청입니다.[C-202]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<ProfileUpdateResponse>> updateProfileImage(
		@RequestPart(value = "profileImageFile", required = false) MultipartFile profileImageFile) {

		// 인증된 사용자 ID 가져오기
		Long memberId = memberLoader.getMember().getId();

		// 파일 업로드 및 프로필 업데이트 처리
		String profileImageUrl = memberService.updateProfileImage(memberId, profileImageFile);

		// 응답 생성
		ProfileUpdateResponse response = ProfileUpdateResponse.from(profileImageUrl);

		return ResponseEntity.ok(DataResponse.from(response));
	}
}

