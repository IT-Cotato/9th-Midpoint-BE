package middle_point_search.backend.domains.member.controller;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
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
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.common.security.filter.jwtFilter.JwtTokenProvider;
import middle_point_search.backend.common.util.MemberLoader;
import middle_point_search.backend.domains.member.dto.request.CreateMemberRequest;
import middle_point_search.backend.domains.member.dto.request.DeleteMemberRequest;
import middle_point_search.backend.domains.member.dto.request.FindMemberInfoResponse;
import middle_point_search.backend.domains.member.dto.request.LoginMemberRequest;
import middle_point_search.backend.domains.member.dto.request.SendEmailVerificationRequest;
import middle_point_search.backend.domains.member.dto.request.SendNewPasswordRequest;
import middle_point_search.backend.domains.member.dto.request.SendNewPasswordResponse;
import middle_point_search.backend.domains.member.dto.request.SendPasswordReissueVerificationRequest;
import middle_point_search.backend.domains.member.dto.request.UpdateMemberAddressRequest;
import middle_point_search.backend.domains.member.dto.request.UpdateMemberNameRequest;
import middle_point_search.backend.domains.member.dto.request.UpdatePasswordRequest;
import middle_point_search.backend.domains.member.dto.request.VerifyEmailVerificationCodeRequest;
import middle_point_search.backend.domains.member.dto.response.FindProfileImageUrlResponse;
import middle_point_search.backend.domains.member.dto.response.VerifyEmailVerificationCodeResponse;
import middle_point_search.backend.domains.member.service.MemberService;
import middle_point_search.backend.domains.s3.dto.response.CreatePreSignedUrlResponse;

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
			이름, 이메일, 비밀번호, 주소, 인증 코드를 입력받아 회원가입한다.
			
			파리미터 조건
			- 이름은 2자 이상 30자 이하
			- 비밀번호는 20자 이하
			- 이메일은 254자 이하""",
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
	public ResponseEntity<DataResponse<Void>> createMember(@RequestBody @Valid CreateMemberRequest request) {
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
	public ResponseEntity<DataResponse<Void>> logoutMember(HttpServletRequest request) {
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
		@ModelAttribute LoginMemberRequest request
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
			
			파라미터 조건
			- 이메일 값은 비어있으면 안됨
			- 이메일 형식이 맞아야함
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
	public ResponseEntity<DataResponse<Void>> sendEmailVerification(
		@RequestBody @Valid SendEmailVerificationRequest request
	) {
		memberService.validateDuplicatedEmailAndSendEmailVerification(request);

		return ResponseEntity.ok(DataResponse.ok());
	}

	@PostMapping("/verification/signup")
	@Operation(
		summary = "회원가입 email 인증",
		description = """
			email 인증을 확인
			
			파리미터 조건
			- 이메일 값은 비어있으면 안됨
			- 이메일 형식이 맞아야함
			- 인증코드는 비어있으면 안됨""",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
			),
			@ApiResponse(
				responseCode = "400",
				description = "요청 파라미터가 잘못되었습니다.(이메일 형식, 인증코드)[C-202]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
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

	@PatchMapping("/password")
	@Operation(
		summary = "비밀번호 수정",
		description = """
			비밀번호 수정
			
			파라미터 조건
			- 현재 비밀번호는 비어있으면 안됨
			- 새 비밀번호는 비어있으면 안됨
			- 새 비밀번호는 20자 이하
			""",
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
			@ApiResponse(
				responseCode = "403",
				description = "비밀번호가 일치하지 않습니다.[M-005]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<Void>> updatePassword(
		@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest
	) {
		Long memberId = memberLoader.getMemberId();

		memberService.updatePassword(memberId, updatePasswordRequest.password(), updatePasswordRequest.newPassword());

		return ResponseEntity.ok(DataResponse.ok());
	}

	@PostMapping("/verification-request/password-reissue")
	@Operation(
		summary = "비밀번호 재발급 email 인증 요청",
		description = """
			이메일을 입력받아 비밀번호 재발급 인증코드를 전송합니다.
			
			파라미터 조건
			- 이메일 값은 비어있으면 안됨
			""",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
			),
			@ApiResponse(
				responseCode = "400",
				description = "요청 파라미터가 잘못되었습니다.[C-202], 비밀번호는 비어있으면 안됨. 새 비밀번호는 20자 이하",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "404",
				description = "존재하지 않는 회원입니다.[M-002]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "500",
				description = "이메일 전송에 실패하였습니다.[E-001]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
		}
	)
	public ResponseEntity<DataResponse<Void>> sendPasswordReissueVerification(
		@RequestBody @Valid SendPasswordReissueVerificationRequest request
	) {
		memberService.sendPasswordReissueVerification(request);

		return ResponseEntity.ok(DataResponse.ok());
	}

	@PostMapping("/password-reissue")
	@Operation(
		summary = "비밀번호 재발급",
		description = """
			인증 번호를 통해 비밀번호를 재발급한다.
			
			파라미터 조건
			- 비밀번호는 비어있으면 안됨
			- 새 비밀번호는 20자 이하""",
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
			@ApiResponse(
				responseCode = "403",
				description = "이메일 인증을 먼저 진행해주세요.[M-003]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "403",
				description = "인증 코드가 일치하지 않습니다.[M-004]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "500",
				description = "이메일 전송에 실패하였습니다.[E-001]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
		}
	)
	public ResponseEntity<DataResponse<SendNewPasswordResponse>> sendNewPassword(
		@RequestBody @Valid SendNewPasswordRequest request
	) {
		SendNewPasswordResponse response = memberService.validateCodeAndSendNewPassword(request);

		return ResponseEntity.ok(DataResponse.from(response));
	}

	@GetMapping("/info")
	@Operation(
		summary = "회원정보 조회",
		description = "회원정보 조회",
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
			)
		}
	)
	public ResponseEntity<DataResponse<FindMemberInfoResponse>> findMemberInfo() {
		Long memberId = memberLoader.getMemberId();

		FindMemberInfoResponse response = memberService.findMemberInfo(memberId);

		return ResponseEntity.ok(DataResponse.from(response));
	}

	@PatchMapping("/name")
	@Operation(
		summary = "닉네임 수정",
		description = """
			닉네임 수정
			
			파라미터 조건
			- 이름은 2자 이상 30자 이하
			""",
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
			@ApiResponse(
				responseCode = "401",
				description = "인증에 실패하였습니다.[C-101]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "402",
				description = "Access Token을 재발급해야합니다.[A-004]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<Void>> updateMemberName(
		@Valid @RequestBody UpdateMemberNameRequest request
	) {
		Long memberId = memberLoader.getMemberId();

		memberService.updateMemberName(memberId, request);

		return ResponseEntity.ok(DataResponse.ok());
	}

	@PatchMapping("/address")
	@Operation(
		summary = "주소 수정",
		description = """
			주소 수정
			
			파라미터 조건
			- 각 값은 비어있으면 안됨
			""",
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
			@ApiResponse(
				responseCode = "401",
				description = "인증에 실패하였습니다.[C-101]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "402",
				description = "Access Token을 재발급해야합니다.[A-004]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<Void>> updateMemberAddress(
		@Valid @RequestBody UpdateMemberAddressRequest request
	) {
		Long memberId = memberLoader.getMemberId();

		memberService.updateMemberAddress(memberId, request);

		return ResponseEntity.ok(DataResponse.ok());
	}

	@DeleteMapping("/address")
	@Operation(
		summary = "주소 삭제",
		description = """
			주소 삭제
			""",
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
			)
		}
	)
	public ResponseEntity<DataResponse<Void>> deleteMemberAddress() {
		Long memberId = memberLoader.getMemberId();

		memberService.deleteMemberAddress(memberId);

		return ResponseEntity.ok(DataResponse.ok());
	}

	// 파일 업로드 전 사전 서명된 URL 생성
	@GetMapping("/profile/presigned")
	@Operation(
		summary = "프로필 사전 서명된 URL 생성",
		description = """
			프로필 사전 서명된 URL을 생성합니다.
			filename은 확장자를 포함해야 됩니다. (ex. test.jpg)
			확장자는 jpg, jpeg, png만 가능합니다.
			""",
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
			@ApiResponse(
				responseCode = "400",
				description = "유효하지 않은 파일 확장자입니다.[S-001]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<CreatePreSignedUrlResponse>> createFilePreSignedUrl(
		@RequestParam("filename") String filename
	) {
		Long memberId = memberLoader.getMemberId();

		CreatePreSignedUrlResponse response = memberService.createProfilePreSignedUrl(memberId, filename);

		return ResponseEntity.ok(DataResponse.from(response));
	}

	// 프로필 조회
	@GetMapping("/profile")
	@Operation(
		summary = "프로필 이미지 조회",
		description = """
			프로필 이미지 조회
			저장된 프로필 이미지가 없으면 isExist는 false, url은 null을 반환합니다.""",
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
			)
		}
	)
	public ResponseEntity<DataResponse<FindProfileImageUrlResponse>> findProfileImageUrl() {
		Long memberId = memberLoader.getMemberId();

		FindProfileImageUrlResponse response = memberService.findProfileImageUrl(memberId);

		return ResponseEntity.ok(DataResponse.from(response));
	}

	// 프로필 삭제
	@DeleteMapping("/profile")
	@Operation(
		summary = "프로필 이미지 삭제",
		description = """
			프로필 이미지 삭제
			프로필 이미지를 삭제합니다.""",
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
			)
		}
	)
	public ResponseEntity<DataResponse<Void>> deleteProfileImage() {
		Long memberId = memberLoader.getMemberId();

		memberService.deleteProfileImage(memberId);

		return ResponseEntity.ok(DataResponse.ok());
	}

	@DeleteMapping("/delete")
	@Operation(
		summary = "회원 탈퇴",
		description = """
			회원 탈퇴
			회원 탈퇴를 진행합니다.""",
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
			@ApiResponse(
				responseCode = "401",
				description = "인증에 실패하였습니다.[C-101]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "402",
				description = "Access Token을 재발급해야합니다.[A-004]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<Void>> deleteMember(
		@RequestBody @Valid DeleteMemberRequest request,
		HttpServletRequest httpServletRequest
	) {
		String accessToken = jwtTokenProvider.extractAccessToken(httpServletRequest)
			.orElseThrow(() -> CustomException.from(REISSUE_ACCESS_TOKEN));

		Long memberId = memberLoader.getMemberId();

		memberService.deleteMember(memberId, request, accessToken);

		return ResponseEntity.ok(DataResponse.ok());
	}
}

