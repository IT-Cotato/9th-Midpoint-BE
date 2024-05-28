package middle_point_search.backend.common.util;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.conf.WebClientConf;
import middle_point_search.backend.common.exception.CustomException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class WebClientUtil {

	private final WebClientConf webClientConf;

	// WebClient Conf 세팅을 이용하며, url과 응답 클래스를 제공하여 요청을 해 Mono로 응답받는 메서드
	public <T> Mono<T> getMono(String url, Class<T> responseDtoClass) {
		return webClientConf.webClient()
			.method(HttpMethod.GET)
			.uri(url)
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError,
				clientResponse -> Mono.error(new CustomException(API_UNAUTHORIZED)))
			.onStatus(HttpStatusCode::is5xxServerError,
				clientResponse -> Mono.error(new CustomException(API_INTERNAL_SERVER_ERROR)))
			.bodyToMono(responseDtoClass);
	}

	// WebClient Conf 세팅을 이용하며, url과 응답 클래스를 제공하여 요청을 해 Flux로 응답받는 메서드
	public <T> Flux<T> getFlux(String url, Class<T> responseDtoClass) {
		return webClientConf.webClient()
			.method(HttpMethod.GET)
			.uri(url)
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError,
				clientResponse -> Mono.error(new CustomException(API_UNAUTHORIZED)))
			.onStatus(HttpStatusCode::is5xxServerError,
				clientResponse -> Mono.error(new CustomException(API_INTERNAL_SERVER_ERROR)))
			.bodyToFlux(responseDtoClass);
	}
}
