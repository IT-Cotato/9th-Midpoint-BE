package middle_point_search.backend.domains.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.dto.DataResponse;
import middle_point_search.backend.domains.member.dto.request.OAuthLoginRequest;
import middle_point_search.backend.domains.member.dto.response.LoginMemberResponse;
import middle_point_search.backend.domains.member.service.OAuthService;

@RestController
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
public class OAuthController {

	private final OAuthService oAuthService;

	@PostMapping("/login/{provider}")
	public ResponseEntity<DataResponse<LoginMemberResponse>> login(
		@PathVariable String provider,
		@RequestBody OAuthLoginRequest request
	) {
		LoginMemberResponse response = oAuthService.login(provider, request.code());

		return ResponseEntity.ok(DataResponse.from(response));
	}
}
