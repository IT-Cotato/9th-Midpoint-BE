package middle_point_search.backend.common.security.jwt.service;

import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface JwtService {

	String createAccessToken(String name, String roomId);
	String createRefreshToken();

	void updateRefreshToken(String name, String roomId, String refreshToken);

	void destroyRefreshToken(String name, String roomId);

	void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken);
	void sendAccessToken(HttpServletResponse response, String accessToken);

	Optional<String> extractAccessToken(HttpServletRequest request);

	Optional<String> extractRefreshToken(HttpServletRequest request);

	void setAccessTokenHeader(HttpServletResponse response, String accessToken);

	void setRefreshTokenHeader(HttpServletResponse response, String refreshToken);

	boolean isTokenValid(String token);

}