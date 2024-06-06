package middle_point_search.backend.common.security.jwt.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.security.dto.CustomUserDetails;
import middle_point_search.backend.common.security.jwt.service.JwtService;
import middle_point_search.backend.domains.member.repository.MemberRepository;

@Slf4j
@RequiredArgsConstructor
public class JwtLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final JwtService jwtService;
	private final MemberRepository memberRepository;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {
		String name = extractName(authentication);
		String roomId = extractRoomId(authentication);
		String accessToken = jwtService.createAccessToken(name, roomId);
		String refreshToken = jwtService.createRefreshToken();

		jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
		memberRepository.findByRoom_IdentityNumberAndName(roomId, name).ifPresent(
			member -> member.updateRefreshToken(refreshToken)
		);

		log.info( "로그인에 성공합니다. name: {}" , name);
		log.info("accessToken={}", accessToken);
		log.info("refreshToken={}", refreshToken);
	}

	private String extractName(Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		return userDetails.getUsername();
	}

	private String extractRoomId(Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		return userDetails.getRoomId();
	}
}