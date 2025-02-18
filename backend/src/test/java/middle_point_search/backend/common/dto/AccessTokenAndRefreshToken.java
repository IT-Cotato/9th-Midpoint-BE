package middle_point_search.backend.common.dto;

public record AccessTokenAndRefreshToken(
	String accessToken,
	String refreshToken
) {
}
