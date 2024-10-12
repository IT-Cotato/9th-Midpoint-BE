package middle_point_search.backend.common.security.filter.jwtFilter;

import java.util.Optional;

import org.springframework.security.core.Authentication;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface JwtTokenProvider {

	Authentication getAuthentication(String accessToken);

	String createAccessToken(Long memberId);

	String createRefreshToken();

	void updateRefreshToken(Long memberId, String refreshToken);

	void destroyRefreshToken(Long memberId);

	void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken);

	void sendAccessToken(HttpServletResponse response, String accessToken);

	Optional<String> extractAccessToken(HttpServletRequest request);

	Optional<String> extractRefreshToken(HttpServletRequest request);

	Claims parseClaims(String accessToken);

	Optional<Long> extractMemberId(String accessToken);

	void setAccessTokenHeader(HttpServletResponse response, String accessToken);

	void setRefreshTokenHeader(HttpServletResponse response, String refreshToken);

	boolean isTokenValid(String token);

	void checkRefreshTokenAndReIssueAccessAndRefreshToken(HttpServletResponse response, String refreshToken);

	boolean isLogout(String accessToken);
}