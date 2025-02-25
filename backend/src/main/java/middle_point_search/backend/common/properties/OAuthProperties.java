package middle_point_search.backend.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "oauth")
public class OAuthProperties {

	private Kakao kakao;
	private Naver naver;
	private Google google;

	@Getter
	@Setter
	public static class Kakao {
		private String clientId;
		private String clientSecret;
		private String redirectUri;
	}

	@Getter
	@Setter
	public static class Naver {
		private String clientId;
		private String clientSecret;
		private String redirectUri;
	}

	@Getter
	@Setter
	public static class Google {
		private String clientId;
		private String clientSecret;
		private String redirectUri;
	}
}
