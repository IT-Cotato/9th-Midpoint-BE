package middle_point_search.backend.common.security.conf;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import middle_point_search.backend.common.security.cors.constants.CorsProperties;
import middle_point_search.backend.common.security.jwt.constants.JwtProperties;

// 전역적으로 사용되는 상수
@Configuration
@EnableConfigurationProperties(value = {JwtProperties.class, CorsProperties.class})
public class PropertyConfig {
}
