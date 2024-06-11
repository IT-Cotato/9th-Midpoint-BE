package middle_point_search.backend.common.security.login.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.security.dto.MemberAuthenticationToken;
import middle_point_search.backend.common.security.jwt.provider.JwtTokenProvider;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {

		String roomId = extractRoomId((MemberAuthenticationToken)authentication);
		String name = extractName((MemberAuthenticationToken)authentication);

		String accessToken = jwtTokenProvider.createAccessToken(roomId, name);
		String refreshToken = jwtTokenProvider.createRefreshToken();

		jwtTokenProvider.sendAccessAndRefreshToken(response, accessToken, refreshToken);
		jwtTokenProvider.destroyRefreshToken(roomId, name);

		log.info("로그인 성공: {}, {}", roomId, name);
		log.info("accessToken={}", accessToken);
		log.info("refreshToken={}", refreshToken);
	}

	private String extractName(MemberAuthenticationToken authentication) {
		return (String)authentication.getPrincipal();
	}

	private String extractRoomId(MemberAuthenticationToken authentication) {
		return (String)authentication.getRoomId();
	}
}

