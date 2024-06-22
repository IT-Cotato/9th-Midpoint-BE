package middle_point_search.backend.common.security.cors.constants;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {

	private List<String> allowedUrls;
	private Boolean requireCredential;
	private List<String> allowedOrigins;
	private List<String> allowedMethods;
	private List<String> allowedHeaders;
}
