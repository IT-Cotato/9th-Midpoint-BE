package middle_point_search.backend.common.webClient.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.properties.KakaoProperties;
import middle_point_search.backend.common.properties.MarketProperties;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebClientConf {

	private final MarketProperties marketProperties;
	private final KakaoProperties kakaoProperties;


	private HttpClient httpClient = HttpClient.create()
		.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000); // 5초

	@Bean
	public WebClient webClientForMarket() {
		DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(marketProperties.getBaseUrl());
		factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
		return WebClient.builder()
			.uriBuilderFactory(factory) //secret Key 인코딩되는 문제 방지하기 위해, DefaultUriBuilderFactory를 이욯
			.codecs(configurer -> configurer.defaultCodecs()
				.maxInMemorySize(2 * 1024 * 1024)) // 응답 payload가 클 경우 나는 에러 방지, 최대 2MB
			.clientConnector(new ReactorClientHttpConnector(httpClient))
			.build();
	}

	@Bean
	public WebClient webClientForKakao() {
		DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
		factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
		return WebClient.builder()
			.uriBuilderFactory(factory)
			.baseUrl(kakaoProperties.getBaseUrl())
			.codecs(configurer -> configurer.defaultCodecs()
				.maxInMemorySize(2 * 1024 * 1024)) // 응답 payload가 클 경우 나는 에러 방지, 최대 2MB
			.clientConnector(new ReactorClientHttpConnector(httpClient))
			.build();
	}
}
