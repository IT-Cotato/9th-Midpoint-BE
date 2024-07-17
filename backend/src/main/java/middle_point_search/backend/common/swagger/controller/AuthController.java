package middle_point_search.backend.common.swagger.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
public class AuthController {
	@PostMapping("/api/auth/login")
	@Operation(
		summary = "로그인",
		description = "사용자 이름, 비밀번호, 방 ID를 사용하여 로그인",
		requestBody = @RequestBody(
			description = "로그인 데이터",
			required = true,
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LoginRequest.class))
		)
	)
	public void fakeLoginEndpoint() {
		// 이 메소드는 실제로 실행되지 않습니다. 문서용도로만 사용됩니다.
	}

	static class LoginRequest {
		public String name;
		public String pw;
		public String roomId;
	}
}
