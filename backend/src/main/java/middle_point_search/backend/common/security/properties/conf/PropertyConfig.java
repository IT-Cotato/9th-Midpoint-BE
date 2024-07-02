package middle_point_search.backend.common.security.properties.conf;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import middle_point_search.backend.common.security.properties.CorsProperties;
import middle_point_search.backend.common.security.properties.JwtProperties;
import middle_point_search.backend.common.security.properties.SecurityProperties;

// 전역적으로 사용되는 상수
@Configuration
@EnableConfigurationProperties(value = {JwtProperties.class, CorsProperties.class, SecurityProperties.class})
public class PropertyConfig {
}
