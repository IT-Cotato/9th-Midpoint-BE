package middle_point_search.backend.domains.member.dto;

public record AccessTokenAndRefreshToken(
	String accessToken,
	String refreshToken
) {
}
