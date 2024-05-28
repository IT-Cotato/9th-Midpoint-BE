package middle_point_search.backend.common.conf;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import io.netty.channel.ChannelOption;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
public class WebClientConf {

	DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();

	HttpClient httpClient = HttpClient.create()
		.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000); // 5초

	@Bean
	public WebClient webClient() {
		factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
		return WebClient.builder()
			.uriBuilderFactory(factory)
			.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024)) // 응답 payload가 클 경우 나는 에러 방지, 최대 2MB
			.clientConnector(new ReactorClientHttpConnector(httpClient))
			.build();
	}

	@Bean
	public ConnectionProvider connectionProvider() {
		return ConnectionProvider.builder("http-pool")
			.maxConnections(100)
			.pendingAcquireTimeout(Duration.ofMillis(0))
			.pendingAcquireMaxCount(-1)
			.maxIdleTime(Duration.ofMillis(1000L))
			.build();
	}
}
