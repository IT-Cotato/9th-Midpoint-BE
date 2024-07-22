package middle_point_search.backend.common.security.jwt.filter;

import static middle_point_search.backend.common.exception.errorCode.CommonErrorCode.UNAUTHORIZED;
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
import middle_point_search.backend.common.properties.SecurityProperties;
import middle_point_search.backend.common.security.jwt.provider.JwtTokenProvider;
import middle_point_search.backend.domains.member.repository.MemberRepository;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final MemberRepository memberRepository;
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
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException, CustomException {

		final String refreshToken = jwtTokenProvider.extractRefreshToken(request).orElse(null);
		final String accessToken = jwtTokenProvider.extractAccessToken(request).orElse(null);

		//1. access토큰이 존재하며, accessToken이 유효하면 인증
		//2. access토큰이 존재하며, accesToken이 유효하지 않으면 에러 리턴
		//3. refresh토큰이 존재하며, refreshToken이 유효하면 access 토큰 재발급
		//4. refresh토큰이 존재하며, refreshToken이 유효하지 않으면 로그인 유도
		//5. 그 외 모든 경우는 에러 리턴
		if (accessToken != null && jwtTokenProvider.isTokenValid(accessToken)) {
			log.info("access토큰 인증 성공");

			//토큰이 logout된 토큰인지 검사
			if (jwtTokenProvider.isLogout(accessToken)) {
				throw new CustomException(INVALID_ACCESS_TOKEN);
			}

			Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
			saveAuthentication(authentication);
			filterChain.doFilter(request, response);
		} else if (accessToken != null && !jwtTokenProvider.isTokenValid(accessToken)) {
			log.info("access토큰 인증 실패");
			throw new CustomException(INVALID_ACCESS_TOKEN);
		} else if (refreshToken != null && jwtTokenProvider.isTokenValid(refreshToken)) {
			log.info("refresh토큰 인증 성공");
			jwtTokenProvider.checkRefreshTokenAndReIssueAccessAndRefreshToken(response, refreshToken);
		} else if (refreshToken != null && !jwtTokenProvider.isTokenValid(refreshToken)) {
			log.info("refresh토큰 인증 실패");
			throw new CustomException(INVALID_REFRESH_TOKEN);
		} else {
			log.info("인증 실패");
			throw new CustomException(UNAUTHORIZED);
		}
	}

	private void saveAuthentication(Authentication authentication) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}
}
