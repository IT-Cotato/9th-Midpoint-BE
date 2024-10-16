package middle_point_search.backend.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "kakao")
public class KakaoProperties {

	private String key;
	private String baseUrl;
	private String keywordSearchUrl;
	private String categorySearchUrl;
	private String paramX;
	private String paramY;
	private String paramRadius;
	private String paramPage;
	private String paramSize;
	private String paramSort;
	private String paramGroup;
	private String paramQuery;

	private String radius;
	private Integer size;
	private String sort;
}
