package middle_point_search.backend.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "market")
public class MarketProperties {

	private String key;
	private String baseUrl;
	private String marketApiCountUrl;
	private String marketApiUrl;
	private Integer marketApiRequestUnit;
}
