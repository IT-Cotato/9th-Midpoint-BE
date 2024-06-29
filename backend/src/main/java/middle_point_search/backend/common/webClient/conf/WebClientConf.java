package middle_point_search.backend.common.webClient.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.properties.KakaoProperties;
import middle_point_search.backend.common.properties.MarketProperties;
import reactor.netty.http.client.HttpClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConf {

	private final MarketProperties marketProperties;
	private final KakaoProperties kakaoProperties;

	private DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
	private HttpClient httpClient = HttpClient.create()
		.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000); // 5초

	@Bean
	public WebClient webClientForMarket() {
		factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
		return WebClient.builder()
			.baseUrl(marketProperties.getBaseUrl())
			.uriBuilderFactory(factory)
			.codecs(configurer -> configurer.defaultCodecs()
				.maxInMemorySize(2 * 1024 * 1024)) // 응답 payload가 클 경우 나는 에러 방지, 최대 2MB
			.clientConnector(new ReactorClientHttpConnector(httpClient))
			.build();
	}

	@Bean
	public WebClient webClientForKakao() {
		factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
		return WebClient.builder()
			.baseUrl(kakaoProperties.getBaseUrl())
			.uriBuilderFactory(factory)
			.codecs(configurer -> configurer.defaultCodecs()
				.maxInMemorySize(2 * 1024 * 1024)) // 응답 payload가 클 경우 나는 에러 방지, 최대 2MB
			.clientConnector(new ReactorClientHttpConnector(httpClient))
			.build();
	}
}
