package middle_point_search.backend.domains.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.dto.BaseResponse;
import middle_point_search.backend.common.util.MemberLoader;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

	private final MemberService memberService;
	private final MemberLoader memberLoader;

	@PostMapping("/logout")
	@Operation(
		summary = "로그아웃",
		description = "로그아웃한다. AccessToken 필요"
	)
	public ResponseEntity<BaseResponse> memberLogout() {
		Member member = memberLoader.getMember();

		memberService.logoutMember(member);

		return ResponseEntity.ok(BaseResponse.ok());
	}
}

