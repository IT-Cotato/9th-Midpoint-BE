package middle_point_search.backend.common.security.jwt.service;

import java.util.Optional;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface JwtService {

	String createAccessToken(String roomId, String name);
	String createRefreshToken();

	void updateRefreshToken(String roomId, String name, String refreshToken);

	void destroyRefreshToken(String roomId, String name);

	void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken);
	void sendAccessToken(HttpServletResponse response, String accessToken);

	Optional<String> extractAccessToken(HttpServletRequest request);

	Optional<String> extractRefreshToken(HttpServletRequest request);

	Claims parseClaims(String accessToken);

	void setAccessTokenHeader(HttpServletResponse response, String accessToken);

	void setRefreshTokenHeader(HttpServletResponse response, String refreshToken);

	boolean isTokenValid(String token);

}