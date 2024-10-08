package middle_point_search.backend.domains.market.service;

import java.util.List;
import java.util.Objects;

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
import middle_point_search.backend.domains.market.repository.MarketRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MarketService {

	private final MarketRepository marketRepository;

	private final MarketProperties marketProperties;

	private final WebClientUtil webClientUtil;

	// Market 정보 업데이트하기
	@Transactional(rollbackFor = {CustomException.class})
	public void updateMarket() {
		marketRepository.deleteAllMarket();

		int marketCount = getMarketCount();
		int totalPage = parseCountToPage(marketCount);

		requestAndSaveMarkets(totalPage);
	}

	// Market 총 데이터 수 가져오기
	private int getMarketCount() {
		long startTime = System.currentTimeMillis();
		String url = marketProperties.getMarketApiUrl();

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add(marketProperties.getParamPage(), "1");
		params.add(marketProperties.getParamPerPage(), "1");

		MarketApiResponse response = webClientUtil.getMarket(url, params, MarketApiResponse.class);

		if (!Objects.nonNull(response)) {
			throw new CustomException(CommonErrorCode.EXTERNAL_SERVER_ERROR);
		}

		long stopTime = System.currentTimeMillis();
		log.info("getMarketCount | time = {}ms", stopTime - startTime);
		return response.getTotalCount();
	}

	// market api를 요청하고 가져온 데이터를 저장하는 메서드
	private void requestAndSaveMarkets(int totalPage) {
		long startTime = System.currentTimeMillis();
		for (int i = 1; i <= totalPage; i++) {
			String url = marketProperties.getMarketApiUrl();

			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add(marketProperties.getParamPage(), String.valueOf(i));
			params.add(marketProperties.getParamPerPage(), marketProperties.getMarketApiRequestUnit().toString());

			MarketApiResponse response = webClientUtil.getMarket(url, params, MarketApiResponse.class);

			if (!Objects.nonNull(response)) {
				throw new CustomException(CommonErrorCode.EXTERNAL_SERVER_ERROR);
			}

			List<Market> markets = response.getData().stream().map(Market::from).distinct().toList();

			saveAllMarket(markets);
		}

		long stopTime = System.currentTimeMillis();
		log.info("requestAndSaveMarkets | time = {}ms", stopTime - startTime);
	}

	// 데이터 개수를 페이지로 변환해주는 메서드(개수가 2001이면 1,2,3 페이지로 3 페이지가 리턴)
	private int parseCountToPage(int count) {
		if (count % marketProperties.getMarketApiRequestUnit() == 0) {
			return count / marketProperties.getMarketApiRequestUnit();
		}

		return count / marketProperties.getMarketApiRequestUnit() + 1;
	}

	// Market List 저장
	private void saveAllMarket(List<Market> markets) {
		long startTime = System.currentTimeMillis();
		marketRepository.saveAll(markets);
		long stopTime = System.currentTimeMillis();
		log.info("saveAllMarket | time = {}ms", stopTime - startTime);
	}
}
