package middle_point_search.backend.common.security.filter.jwtFilter;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.common.exception.errorCode.CommonErrorCode;
import middle_point_search.backend.common.properties.SecurityProperties;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	private final SecurityProperties securityProperties;
	private final AntPathMatcher pathMatcher;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String path = request.getRequestURI();

		boolean result = Arrays.stream(securityProperties.getPermitUrls())
			.anyMatch(permitUrl -> pathMatcher.match(permitUrl, path));

		log.info("JwtAuthenticationFilter.shouldNotFilter({}) : {}", path, result);

		return result;
	}

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException, CustomException {

		final String refreshToken = jwtTokenProvider.extractRefreshToken(request).orElse(null);
		final String accessToken = jwtTokenProvider.extractAccessToken(request).orElse(null);

		//1. refresh토큰이 존재하며, refreshToken이 유효하면 access 토큰 재발급
		//2. refresh토큰이 존재하며, refreshToken이 유효하지 않으면 로그인 유도
		//3. access토큰이 존재하며, accessToken이 유효하면 인증
		//4. access토큰이 존재하며, accesToken이 유효하지 않으면 에러 리턴
		//5. 그 외 모든 경우는 에러 리턴
		if (refreshToken != null && jwtTokenProvider.isTokenValid(refreshToken)) {
			log.info("refresh토큰 인증 성공");
			jwtTokenProvider.checkRefreshTokenAndReIssueAccessAndRefreshToken(response, refreshToken);
		} else if (refreshToken != null && !jwtTokenProvider.isTokenValid(refreshToken)) {
			log.info("refresh토큰 인증 실패");
			throw CustomException.from(INVALID_REFRESH_TOKEN);
		} else if (accessToken != null && jwtTokenProvider.isTokenValid(accessToken)) {
			checkLogoutToken(accessToken);

			log.info("access토큰 인증 성공");
			Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
			saveAuthentication(authentication);
			filterChain.doFilter(request, response);
		} else if (accessToken != null && !jwtTokenProvider.isTokenValid(accessToken)) {
			log.info("access토큰 인증 실패");
			throw CustomException.from(REISSUE_ACCESS_TOKEN);
		} else {
			log.info("인증 실패");
			throw CustomException.from(CommonErrorCode.UNAUTHORIZED);
		}
	}

	private void saveAuthentication(Authentication authentication) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}

	//토큰이 logout된 토큰인지 검사
	private void checkLogoutToken(String accessToken) {
		if (jwtTokenProvider.isLogout(accessToken)) {
			log.info("logout된 accessToken으로 인증 실패");
			throw CustomException.from(INVALID_ACCESS_TOKEN);
		}
	}
}
