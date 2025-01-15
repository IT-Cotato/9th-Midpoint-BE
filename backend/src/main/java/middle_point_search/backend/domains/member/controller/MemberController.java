package middle_point_search.backend.domains.member.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
import middle_point_search.backend.domains.member.dto.MemberDTO.MemberCreateRequest;
import middle_point_search.backend.domains.member.dto.request.SendEmailVerificationRequest;
import middle_point_search.backend.domains.member.dto.request.VerifyEmailVerificationCodeRequest;
import middle_point_search.backend.domains.member.dto.response.VerifyEmailVerificationCodeResponse;
import middle_point_search.backend.domains.member.service.MemberService;

@Tag(name = "MEMBER API", description = "회원에 대한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

	private final MemberService memberService;
	private final MemberLoader memberLoader;
	private final JwtTokenProvider jwtTokenProvider;

	@PostMapping
	@Operation(
		summary = "회원가입",
		description = """
			회원가입한다.
			
			이름, 이메일, 비밀번호, 주소, 인증 코드를 입력받아 회원가입한다.""",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
			),
			@ApiResponse(
				responseCode = "409",
				description = "이미 존재하는 이메일입니다.[M-001]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "403",
				description = "인증 코드가 일치하지 않습니다.[M-004]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "403",
				description = "이메일 인증을 먼저 진행해주세요.[M-003]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<Void>> memberCreate(@RequestBody @Valid MemberCreateRequest request) {
		memberService.createMember(request);

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
		Long memberId = memberLoader.getMemberId();

		memberService.logoutMember(memberId, accessToken);

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

	@PostMapping("/verification-request/signup")
	@Operation(
		summary = "회원가입 email 인증 요청",
		description = """
			이메일을 입력받아 인증코드를 전송합니다.
			이메일 인증 확인 API로 타당한 인증코드인지 확인합니다.
			회원가입 시 인증코드를 함께 보냅니다.
			""",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
			),
			@ApiResponse(
				responseCode = "409",
				description = "이미 존재하는 이메일입니다.[M-001]",
				content = @Content(schema = @Schema(implementation = org.springframework.web.ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "500",
				description = "이메일 전송에 실패하였습니다.[E-001]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
		}
	)
	public ResponseEntity<DataResponse<Void>> SendEmailVerificationRequest(
		@RequestBody @Valid SendEmailVerificationRequest request
	) {
		memberService.validateDuplicatedEmailAndSendEmailVerification(request);

		return ResponseEntity.ok(DataResponse.ok());
	}

	@PostMapping("/verification/signup")
	@Operation(
		summary = "회원가입 email 인증",
		description = "email 인증을 확인",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
			),
			@ApiResponse(
				responseCode = "403",
				description = "이메일 인증을 먼저 진행해주세요.[M-003]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<VerifyEmailVerificationCodeResponse>> verifyEmailVerificationCode(
		@RequestBody @Valid VerifyEmailVerificationCodeRequest request
	) {
		VerifyEmailVerificationCodeResponse response = memberService.verifyEmailVerificationCode(request);

		return ResponseEntity.ok(DataResponse.from(response));
	}
}

