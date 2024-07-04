package middle_point_search.backend.domains.market.service;

import static java.nio.charset.StandardCharsets.*;

import java.net.URLEncoder;
import java.util.Collections;
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
import middle_point_search.backend.common.properties.KakaoProperties;
import middle_point_search.backend.common.properties.MarketProperties;
import middle_point_search.backend.common.webClient.util.WebClientUtil;
import middle_point_search.backend.domains.market.domain.Market;
import middle_point_search.backend.domains.market.domain.PlaceStandard;
import middle_point_search.backend.domains.market.dto.request.RecommendPlacesFindRequest;
import middle_point_search.backend.domains.market.dto.response.KakaoSearchResponse;
import middle_point_search.backend.domains.market.dto.response.KakaoSearchResponse.Document;
import middle_point_search.backend.domains.market.dto.response.MarketApiResponse;
import middle_point_search.backend.domains.market.dto.response.RecommendPlacesFindResponse;
import middle_point_search.backend.domains.market.repository.MarketRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MarketService {

	private final MarketRepository marketRepository;

	private final MarketProperties marketProperties;
	private final KakaoProperties kakaoProperties;

	private final WebClientUtil webClientUtil;

	@Transactional
	public void updateMarket() {

		log.info("{}", marketProperties.getBaseUrl());
		marketRepository.deleteAll();

		int marketCount = getMarketCount();

		log.info("{}", marketCount);
		int totalPage = parseCountToPage(marketCount);

		requestAndSaveMarkets(totalPage);
	}

	private int getMarketCount() {
		String url = marketProperties.getMarketApiUrl();

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add(marketProperties.getParamKey(), marketProperties.getKey());
		params.add(marketProperties.getParamPage(), "1");
		params.add(marketProperties.getParamPerPage(), "1");

		MarketApiResponse response = webClientUtil.getMarket(url, params, MarketApiResponse.class);

		if (!Objects.nonNull(response)) {
			throw new CustomException(CommonErrorCode.EXTERNAL_SERVER_ERROR);
		}

		return response.getTotalCount();
	}

	// market api를 요청하고 가져온 데이터를 저장하는 메서드
	private void requestAndSaveMarkets(int totalPage) {
		for (int i = 1; i <= totalPage; i++) {
			String url = marketProperties.getMarketApiUrl();

			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add(marketProperties.getParamKey(), marketProperties.getKey());
			params.add(marketProperties.getParamPage(), String.valueOf(i));
			params.add(marketProperties.getParamPerPage(), marketProperties.getMarketApiRequestUnit().toString());

			MarketApiResponse response = webClientUtil.getMarket(url, params, MarketApiResponse.class);

			if (!Objects.nonNull(response)) {
				throw new CustomException(CommonErrorCode.EXTERNAL_SERVER_ERROR);
			}

			List<Market> markets = response.getData()
				.stream()
				.map(Market::from)
				.distinct()
				.toList();

			saveAllMarket(markets);
		}
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
		marketRepository.saveAll(markets);
	}

	// 키워드로 주위 장소 조회
	public List<RecommendPlacesFindResponse> findRecommendPlaces(RecommendPlacesFindRequest request) {
		String x = request.getAddressLong().toString();
		String y = request.getAddressLat().toString();
		String page = request.getPage().toString();
		String size = kakaoProperties.getSize();

		List<Document> response;
		if (request.getPlaceStandard() == PlaceStandard.ALL) {
			response = getKaKaoForAll(x, y, page, size);
		} else if (request.getPlaceStandard() == PlaceStandard.STUDY) {
			response = getKaKaoForStudy(x, y, page, size);
		} else if (request.getPlaceStandard() == PlaceStandard.CAFE){
			response = getKaKaoForCafe(x, y, page, size);
		} else if (request.getPlaceStandard() == PlaceStandard.RESTAURANT) {
			response = getKaKaoForRestaurant(x, y, page, size);
		} else {
			throw new CustomException(CommonErrorCode.INVALID_PARAMETER);
		}

		return response.stream()
			.map(RecommendPlacesFindResponse::from)
			.toList();
	}

	//Cafe, Study, Restaurant 한번에 가져오기
	private List<Document> getKaKaoForAll(String x, String y, String page, String size) {
		String cafeSize =  String.valueOf(Integer.parseInt(size) / 3);
		String restaurantSize = String.valueOf(Integer.parseInt(size) - Integer.parseInt(size) / 3 * 2);
		String studySize = cafeSize;

		List<Document> document1 = getKaKaoForStudy(x, y, page, studySize);
		List<Document> document2 = getKaKaoForCafe(x, y, page, cafeSize);
		List<Document> document3 = getKaKaoForRestaurant(x, y, page, restaurantSize);

		document1.addAll(document2);
		document1.addAll(document3);

		Collections.sort(document1);

		return document1;
	}

	//Cafe 가져오기
	private List<Document> getKaKaoForCafe(String x, String y, String page, String size) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add(kakaoProperties.getParamX(), x);
		params.add(kakaoProperties.getParamY(), y);
		params.add(kakaoProperties.getParamRadius(), kakaoProperties.getRadius());
		params.add(kakaoProperties.getParamSize(), size);
		params.add(kakaoProperties.getParamPage(), page);
		params.add(kakaoProperties.getParamGroup(), PlaceStandard.CAFE.getCode());

		String url = kakaoProperties.getCategorySearchUrl();

		return webClientUtil.getKakao(url, params, KakaoSearchResponse.class).getDocuments();
	}

	//Study 가져오기
	private List<Document> getKaKaoForStudy(String x, String y, String page, String size) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add(kakaoProperties.getParamX(), x);
		params.add(kakaoProperties.getParamY(), y);
		params.add(kakaoProperties.getParamRadius(), kakaoProperties.getRadius());
		params.add(kakaoProperties.getParamSize(), size);
		params.add(kakaoProperties.getParamPage(), page);

		params.add(kakaoProperties.getParamQuery(), URLEncoder.encode("스터디", UTF_8));

		String url = kakaoProperties.getKeywordSearchUrl();

		return webClientUtil.getKakao(url, params, KakaoSearchResponse.class).getDocuments();
	}

	//Restaurant 가져오기
	private List<Document> getKaKaoForRestaurant(String x, String y, String page, String size) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add(kakaoProperties.getParamX(), x);
		params.add(kakaoProperties.getParamY(), y);
		params.add(kakaoProperties.getParamRadius(), kakaoProperties.getRadius());
		params.add(kakaoProperties.getParamSize(), size);
		params.add(kakaoProperties.getParamPage(), page);
		params.add(kakaoProperties.getParamGroup(), PlaceStandard.RESTAURANT.getCode());

		String url = kakaoProperties.getCategorySearchUrl();

		return webClientUtil.getKakao(url, params, KakaoSearchResponse.class).getDocuments();
	}
}
