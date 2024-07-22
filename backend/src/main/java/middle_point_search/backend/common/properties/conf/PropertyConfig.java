package middle_point_search.backend.common.properties.conf;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import middle_point_search.backend.common.properties.JwtProperties;
import middle_point_search.backend.common.properties.KakaoProperties;
import middle_point_search.backend.common.properties.MarketProperties;
import middle_point_search.backend.common.properties.RedisProperties;
import middle_point_search.backend.common.properties.SecurityProperties;
import middle_point_search.backend.common.properties.CorsProperties;

// 전역적으로 사용되는 상수
@Configuration
@EnableConfigurationProperties(value = {
	JwtProperties.class,
	CorsProperties.class,
	SecurityProperties.class,
	MarketProperties.class,
	KakaoProperties.class,
	RedisProperties.class
})
public class PropertyConfig {
}
