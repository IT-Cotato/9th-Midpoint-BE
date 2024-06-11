package middle_point_search.backend.common.security.jwt.filter;

import static middle_point_search.backend.common.exception.errorCode.CommonErrorCode.*;
import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.common.security.conf.SecurityConfig;
import middle_point_search.backend.common.security.jwt.provider.JwtTokenProvider;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.repository.MemberRepository;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final MemberRepository memberRepository;
	private static final List<String> ALLOWED_URLS = List.of(
		"/api/auth/login"
	);

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String path = request.getRequestURI();
		return Arrays.stream(SecurityConfig.PERMIT_URLS).anyMatch(path::startsWith);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		final String refreshToken = jwtTokenProvider.extractRefreshToken(request).orElse(null);
		final String accessToken = jwtTokenProvider.extractAccessToken(request).orElse(null);

		//1. access토큰이 존재하며, accessToken이 유효하면 인증
		//2. access토큰이 존재하며, accesToken이 유효하지 않으면 에러 리턴
		//3. refresh토큰이 존재하며, refreshToken이 유효하면 access 토큰 재발급
		//4. refresh토큰이 존재하며, refreshToken이 유효하지 않으면 로그인 유도
		//5. 그 외 모든 경우는 에러 리턴
		if (accessToken != null && jwtTokenProvider.isTokenValid(accessToken)) {
			Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
			saveAuthentication(authentication);
			filterChain.doFilter(request, response);
		} else if (accessToken != null && !jwtTokenProvider.isTokenValid(accessToken)) {
			throw new CustomException(INVALID_ACCESS_TOKEN);
		} else if (refreshToken != null && jwtTokenProvider.isTokenValid(refreshToken)) {
			checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
		} else if (refreshToken != null && !jwtTokenProvider.isTokenValid(refreshToken)) {
			throw new CustomException(INVALID_REFRESH_TOKEN);
		} else {
			throw new CustomException(INVALID_PARAMETER);
		}
	}

	private boolean isAllowedUrl(String uri) {
		return ALLOWED_URLS.stream().anyMatch(uri::startsWith);
	}

	private void saveAuthentication(Authentication authentication) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}

	private void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
		Member member = memberRepository.findByRefreshToken(refreshToken)
			.orElseThrow(() -> new CustomException(INVALID_ACCESS_TOKEN));

		jwtTokenProvider.sendAccessToken(
			response,
			jwtTokenProvider.createAccessToken(member.getRoom().getIdentityNumber(), member.getName()));
	}
}