package middle_point_search.backend.common.webClient.util;

import static middle_point_search.backend.common.exception.errorCode.CommonErrorCode.*;
import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.common.properties.GoogleProperties;
import middle_point_search.backend.common.properties.KakaoProperties;
import middle_point_search.backend.common.properties.MarketProperties;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebClientUtil {

	private final KakaoProperties kakaoProperties;
	private final MarketProperties marketProperties;
	private final GoogleProperties googleProperties;

	@Qualifier("webClientForMarket")
	private final WebClient webClientForMarket;

	@Qualifier("webClientForKakao")
	private final WebClient webClientForKakao;

	@Qualifier("webClientForGoogle")
	private final WebClient webClientForGoogle;

	// WebClient Conf 세팅을 이용하며, url과 응답 클래스 및 파라미터를 제공하여 요청을 해 Mono로 응답받는 메서드
	public <T> T getMarket(String url, MultiValueMap<String, String> params, Class<T> response) {
		return webClientForMarket
			.method(HttpMethod.GET)
			.uri(uriBuilder -> uriBuilder
				.path(url)
				.queryParam(marketProperties.getParamKey(), marketProperties.getKey())
				.queryParams(params)
				.build())
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
				clientResponse.bodyToMono(String.class)
					.doOnNext(body -> log.warn("4xx error body: {}", body))
					.flatMap(body -> Mono.error(CustomException.from(BAD_REQUEST)))
			)
			.onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
				clientResponse.bodyToMono(String.class)
					.doOnNext(body -> log.error("5xx error body: {}", body))
					.flatMap(body -> Mono.error(CustomException.from(API_INTERNAL_SERVER_ERROR)))
			)
			.bodyToMono(response)
			.block();
	}

	// WebClient Conf 세팅을 이용하며, url과 응답 클래스 및 파라미터를 제공하여 요청을 해 Mono로 응답받는 메서드
	public <T> Mono<T> getMarketByMono(String url, MultiValueMap<String, String> params, Class<T> response) {
		return webClientForMarket
			.method(HttpMethod.GET)
			.uri(uriBuilder -> uriBuilder
				.path(url)
				.queryParam(marketProperties.getParamKey(), marketProperties.getKey())
				.queryParams(params)
				.build())
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
				clientResponse.bodyToMono(String.class)
					.doOnNext(body -> log.warn("4xx error body: {}", body))
					.flatMap(body -> Mono.error(CustomException.from(BAD_REQUEST)))
			)
			.onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
				clientResponse.bodyToMono(String.class)
					.doOnNext(body -> log.error("5xx error body: {}", body))
					.flatMap(body -> Mono.error(CustomException.from(API_INTERNAL_SERVER_ERROR)))
			)
			.bodyToMono(response);
	}

	// WebClient Conf 세팅을 이용하며, url과 응답 클래스 및 파라미터를 제공하여 요청을 해 Mono로 응답받는 메서드
	public <T> T getKakao(String url, MultiValueMap<String, String> params, Class<T> response) {
		return webClientForKakao
			.method(HttpMethod.GET)
			.uri(uriBuilder -> uriBuilder
				.path(url)
				.queryParams(params)
				.build())
			.header(HttpHeaders.AUTHORIZATION, kakaoProperties.getKey())
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
				clientResponse.bodyToMono(String.class)
					.doOnNext(body -> log.warn("kakao 4xx error body: {}", body))
					.flatMap(body -> Mono.error(CustomException.from(BAD_REQUEST)))
			)
			.onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
				clientResponse.bodyToMono(String.class)
					.doOnNext(body -> log.error("kakao 5xx error body: {}", body))
					.flatMap(body -> Mono.error(CustomException.from(API_INTERNAL_SERVER_ERROR)))
			)
			.bodyToMono(response)
			.block();
	}

	// WebClient Conf 세팅을 이용하며, googleMap에 대해 url과 응답 클래스를 제공하여 요청을 해 Mono로 응답받는 메서드
	public <T> T getGoogle(String url, MultiValueMap<String, String> params, Class<T> response) {
		return webClientForGoogle
			.method(HttpMethod.GET)
			.uri(uriBuilder -> uriBuilder
				.path(url)
				.queryParams(params)
				.queryParam(googleProperties.getKeyName(), googleProperties.getKey())
				.build())
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
				clientResponse.bodyToMono(String.class)
					.doOnNext(body -> log.warn("google 4xx error body: {}", body))
					.flatMap(body -> Mono.error(CustomException.from(BAD_REQUEST)))
			)
			.onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
				clientResponse.bodyToMono(String.class)
					.doOnNext(body -> log.error("google 5xx error body: {}", body))
					.flatMap(body -> Mono.error(CustomException.from(API_INTERNAL_SERVER_ERROR)))
			)
			.bodyToMono(response)
			.block();
	}
}
