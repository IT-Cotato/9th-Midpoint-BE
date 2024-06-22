package middle_point_search.backend.common.security.properties;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

	private String[] permitUrls;
}
