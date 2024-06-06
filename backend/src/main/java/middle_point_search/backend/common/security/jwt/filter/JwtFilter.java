package middle_point_search.backend.common.security.jwt.filter;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.security.jwt.constants.JwtProperties;
import middle_point_search.backend.common.security.jwt.provider.JwtTokenProvider;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtProperties jwtProperties;
	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		final String token = getParseJwt(request.getHeader(jwtProperties.getAccess().getHeader()));

		if (token != null && jwtTokenProvider.validateToken(token)) {
			Authentication auth= jwtTokenProvider.getAuthentication(token);
			SecurityContextHolder.getContext().setAuthentication(auth);
		}

		filterChain.doFilter(request, response);
	}

	private String getParseJwt(String authorization) {
		if (StringUtils.hasText(authorization) && authorization.startsWith(jwtProperties.getBEARER())) {
			return authorization.substring(7);
		}
		return null;
	}
}
