package middle_point_search.backend.domains.market.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.common.exception.errorCode.CommonErrorCode;
import middle_point_search.backend.common.properties.MarketProperties;
import middle_point_search.backend.common.webClient.util.WebClientUtil;
import middle_point_search.backend.domains.market.domain.Market;
import middle_point_search.backend.domains.market.dto.response.MarketApiResponse;
import middle_point_search.backend.domains.market.repository.MarketQueryRepository;
import middle_point_search.backend.domains.market.repository.MarketRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MarketService {

	private final MarketQueryRepository marketQueryRepository;
	private final MarketRepository marketRepository;
	private final MarketProperties marketProperties;
	private final WebClientUtil webClientUtil;

	// Market 정보 업데이트하기
	@Transactional(rollbackFor = {CustomException.class})
	public void updateMarket() {
		marketRepository.deleteAllMarket();

		int marketCount = getMarketCount();
		int totalPage = parseCountToPage(marketCount);

		requestAndSaveAllMarkets(totalPage);
	}

	// Market 총 데이터 수 가져오기
	private int getMarketCount() {
		String url = marketProperties.getMarketApiUrl();

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add(marketProperties.getParamPage(), "1");
		params.add(marketProperties.getParamPerPage(), "1");

		MarketApiResponse response = webClientUtil.getMarket(url, params, MarketApiResponse.class);

		return response.getTotalCount();
	}

	// 비동기적으로 모든 페이지의 데이터를 수집하고 저장하는 메서드
	private void requestAndSaveAllMarkets(int totalPage) {
		// 모든 페이지에서 데이터를 비동기로 수집
		List<Mono<MarketApiResponse>> marketApiResponseMonos = new ArrayList<>();
		for (int pageNumber = 1; pageNumber <= totalPage; pageNumber++) {
			marketApiResponseMonos.add(requestMarketPage(pageNumber));
		}

		// 모든 데이터를 모아서 한 번에 저장
		Flux.concat(marketApiResponseMonos)
			.flatMap(response -> Flux.just(response.getData().stream().map(Market::from).distinct().toList()))
			.flatMap(Flux::fromIterable)
			.collectList()
			.subscribe(this::saveAllMarket, error -> {
				throw new CustomException(CommonErrorCode.EXTERNAL_SERVER_ERROR);
			});
	}

	// 특정 페이지의 Market 데이터를 비동기적으로 요청하고 수집하는 메서드
	private Mono<MarketApiResponse> requestMarketPage(int pageNumber) {
		String url = marketProperties.getMarketApiUrl();
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add(marketProperties.getParamPage(), String.valueOf(pageNumber));
		params.add(marketProperties.getParamPerPage(), marketProperties.getMarketApiRequestUnit().toString());

		return webClientUtil.getMarketByMono(url, params, MarketApiResponse.class);
	}

	// 수집된 모든 Market 데이터를 한 번에 저장하는 메서드
	private void saveAllMarket(List<Market> markets) {
		marketQueryRepository.saveAll(markets);
	}

	// 데이터 개수를 페이지로 변환해주는 메서드 (예: 개수가 2001이면 1, 2, 3 페이지로 3 페이지가 리턴)
	private int parseCountToPage(int count) {
		if (count % marketProperties.getMarketApiRequestUnit() == 0) {
			return count / marketProperties.getMarketApiRequestUnit();
		}
		return count / marketProperties.getMarketApiRequestUnit() + 1;
	}
}
