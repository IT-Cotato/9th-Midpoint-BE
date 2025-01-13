package middle_point_search.backend.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "google")
public class GoogleProperties {

	private String key;
	private String keyName;
	private String baseUrl;
	private Map map;

	@Getter
	@Setter
	public static class Map {

		private String distanceMatrixUrl;
		private String origin;
		private String destination;
		private String placeIdUrl;
	}
}
