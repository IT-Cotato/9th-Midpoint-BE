package middle_point_search.backend.common.security.filter.jwtFilter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class JwtDTO {

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class AccessTokenResponse {
		private final String accessToken;

		public static AccessTokenResponse from(String accessToken) {
			return new AccessTokenResponse(accessToken);
		}
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class AccessAndRefreshTokenResponse {
		private final String accessToken;
		private final String refreshToken;

		public static AccessAndRefreshTokenResponse from(String accessToken, String refreshToken) {
			return new AccessAndRefreshTokenResponse(accessToken, refreshToken);
		}
	}
}

