package middle_point_search.backend.common.security.jwt.constants;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Component
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

	private String issuer;
	private String secret;
	private Access access;
	private Refresh refresh;

	private final String ACCESS_TOKEN_SUBJECT = "AccessToken";
	private final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
	private final String NAME_CLAIM = "id";
	private final String ROOM_ID_CLAIM = "roomId";
	private final String BEARER = "Bearer ";

	@Getter
	@RequiredArgsConstructor
	public static class Access {
		private long expiration;
		private String header;
	}

	@Getter
	@RequiredArgsConstructor
	public static class Refresh {
		private long expiration;
		private String header;
	}
}
