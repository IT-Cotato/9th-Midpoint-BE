package middle_point_search.backend.domains.member.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.dto.BaseResponse;
import middle_point_search.backend.common.security.filter.jwtFilter.JwtTokenProvider;
import middle_point_search.backend.common.util.MemberLoader;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.dto.MemberDTO.LoginRequest;
import middle_point_search.backend.domains.member.service.MemberService;

@Tag(name = "MEMBER API", description = "회원에 대한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

	private final MemberService memberService;
	private final MemberLoader memberLoader;
	private final JwtTokenProvider jwtTokenProvider;

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
		}
	)
	public ResponseEntity<BaseResponse> memberLogout(HttpServletRequest request) {
		String accessToken = jwtTokenProvider.extractAccessToken(request).orElse(null);
		Member member = memberLoader.getMember();

		memberService.logoutMember(member, accessToken);

		return ResponseEntity.ok(BaseResponse.ok());
	}

	@PostMapping("/login")
	@Operation(
		summary = "로그인",
		description = "사용자 이름, 비밀번호, 방 ID를 사용하여 로그인",
		requestBody = @RequestBody(
			description = "로그인 데이터",
			required = true,
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LoginRequest.class))
		),
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
			),
			@ApiResponse(
				responseCode = "404",
				description = "해당하는 room이 존재하지 않음"
			),
			@ApiResponse(
				responseCode = "200",
				description = "로그인 실패(보안을 위해 비밀번호가 틀렸어도 200리턴"
			)
		}
	)
	public void fakeLoginEndpoint() {
		// 이 메소드는 실제로 실행되지 않습니다. 문서용도로만 사용됩니다.
	}
}

