package middle_point_search.backend.domains.market.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.util.WebClientUtil;
import middle_point_search.backend.domains.market.domain.Market;
import middle_point_search.backend.domains.market.dto.response.MarketApiResponse;
import middle_point_search.backend.domains.market.repository.MarketRepository;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MarketService {

	private final WebClientUtil webClientUtil;
	private final MarketRepository marketRepository;

	@Value("${market-api-key}")
	private String MARKET_API_SECRET_KEY;

	private final String MARKET_API_COUNT_URL = "https://api.odcloud.kr/api/15090955/v1/uddi:10a9cf5c-77d1-4e5d-b7ff-e527e022612e?page=1&perPage=1&serviceKey=%s";
	private final String MARKET_API_URL = "https://api.odcloud.kr/api/15090955/v1/uddi:10a9cf5c-77d1-4e5d-b7ff-e527e022612e?page=%d&perPage=%d&serviceKey=%s";
	private final int MARKET_API_REQUEST_UNIT = 1000;

	@Transactional
	public void updateMarket() {
		String countUrl = String.format(MARKET_API_COUNT_URL, MARKET_API_SECRET_KEY);

		marketRepository.deleteAll();

		Mono<MarketApiResponse> countMonoResponse = webClientUtil.getMono(countUrl, MarketApiResponse.class);
		countMonoResponse.subscribe(marketApiResponse -> {
			int totalPage = parseCountToPage(marketApiResponse.getTotalCount());

			requestAndSaveMarkets(totalPage);
		});
	}

	// market api를 요청하고 가져온 데이터를 저장하는 메서드
	private void requestAndSaveMarkets(int totalPage) {
		for (int i = 1; i <= totalPage; i++) {
			String url = String.format(MARKET_API_URL, i, MARKET_API_REQUEST_UNIT, MARKET_API_SECRET_KEY);

			Mono<MarketApiResponse> response = webClientUtil.getMono(url, MarketApiResponse.class);

			response.map(MarketApiResponse::getData)
				.map(marketApiDatas -> marketApiDatas.stream().map(Market::from).toList())
				.subscribe(this::saveAllMarket);
		}
	}

	// 데이터 개수를 페이지로 변환해주는 메서드(개수가 2001이면 1,2,3 페이지로 3 페이지가 리턴)
	private int parseCountToPage(int count) {
		if (count % MARKET_API_REQUEST_UNIT == 0) {
			return count / MARKET_API_REQUEST_UNIT;
		}

		return count / MARKET_API_REQUEST_UNIT + 1;
	}

	// Market List 저장
	private void saveAllMarket(List<Market> markets) {
		marketRepository.saveAll(markets);
	}
}
