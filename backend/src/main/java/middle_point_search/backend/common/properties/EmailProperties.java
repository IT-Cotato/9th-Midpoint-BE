package middle_point_search.backend.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "mail")
public class EmailProperties {

	private String host;
	private int port;
	private String username;
	private String password;
	private int connectionTimeout;
	private int timeout;
	private int writeTimeout;
}
