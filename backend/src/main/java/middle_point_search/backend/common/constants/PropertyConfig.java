package middle_point_search.backend.common.constants;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import middle_point_search.backend.common.security.jwt.constants.JwtProperties;

@Configuration
@EnableConfigurationProperties(value = {JwtProperties.class})
public class PropertyConfig {
}
