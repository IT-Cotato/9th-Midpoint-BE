package middle_point_search.backend.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

	private String issuer;
	private String secret;
	private Access access;
	private Refresh refresh;
	private RoomId roomId;

	private String ACCESS_TOKEN_SUBJECT = "AccessToken";
	private String REFRESH_TOKEN_SUBJECT = "RefreshToken";
	private String NAME_CLAIM = "id";
	private String ROOM_ID_CLAIM = "roomId";
	private String BEARER = "Bearer ";

	@Getter
	@Setter
	public static class Access {
		private int expiration;
		private String header;
	}

	@Getter
	@Setter
	public static class Refresh {
		private int expiration;
		private String header;
	}

	@Getter
	@Setter
	public static class RoomId {
		private String header;
	}
}
