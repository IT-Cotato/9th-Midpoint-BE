package middle_point_search.backend.domains.market.service;

import static java.nio.charset.StandardCharsets.*;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
	public Page<RecommendPlacesFindResponse> findRecommendPlaces(RecommendPlacesFindRequest request) {
		String x = request.getAddressLong().toString();
		String y = request.getAddressLat().toString();
		Integer page = request.getPage();
		Integer size = kakaoProperties.getSize();

		Pageable pageable = PageRequest.of(page, size);

		KakaoSearchResponse response = checkPlaceStandardAndGetResponse(request, x, y, pageable);

		int totalCount = response.getMeta().getPageable_count();

		return new PageImpl<>(response.getDocuments().stream()
			.map(RecommendPlacesFindResponse::from)
			.toList(), pageable , totalCount);
	}

	//PlaceStandard에 따른 KakaoSearchResponse를 가져오는 메서드
	private KakaoSearchResponse checkPlaceStandardAndGetResponse(
		RecommendPlacesFindRequest request,
		String x,
		String y,
		Pageable pageable)
	{

		if (request.getPlaceStandard() == PlaceStandard.ALL) {
			return getKaKaoForAll(x, y, pageable);
		} else if (request.getPlaceStandard() == PlaceStandard.STUDY) {
			return getKaKaoForStudy(x, y, pageable);
		} else if (request.getPlaceStandard() == PlaceStandard.CAFE){
			return getKaKaoForCafe(x, y, pageable);
		} else if (request.getPlaceStandard() == PlaceStandard.RESTAURANT) {
			return getKaKaoForRestaurant(x, y, pageable);
		}

		throw new CustomException(CommonErrorCode.INVALID_PARAMETER);
	}

	//Cafe, Study, Restaurant 한번에 가져오기
	private KakaoSearchResponse getKaKaoForAll(String x, String y, Pageable pageable) {
		int size = pageable.getPageSize();
		int page = pageable.getPageNumber();

		int cafeSize =  size / 3;
		int restaurantSize = size - size / 3 * 2;
		int studySize = cafeSize;

		KakaoSearchResponse response1 = getKaKaoForStudy(x, y, PageRequest.of(page, studySize));
		KakaoSearchResponse response2 = getKaKaoForCafe(x, y, PageRequest.of(page, cafeSize));
		KakaoSearchResponse response3 = getKaKaoForRestaurant(x, y, PageRequest.of(page, restaurantSize));

		//위 셋의 응답을 response1로 합치기
		int count1 = response1.getMeta().getPageable_count();
		int count2 = response2.getMeta().getPageable_count();
		int count3 = response3.getMeta().getPageable_count();

		response1.getMeta().setPageable_count(count1 + count2 + count3);

		response1.getDocuments().addAll(response2.getDocuments());
		response1.getDocuments().addAll(response3.getDocuments());

		Collections.sort(response1.getDocuments());

		return response1;
	}

	//Cafe 가져오기
	private KakaoSearchResponse getKaKaoForCafe(String x, String y, Pageable pageable) {
		int size = pageable.getPageSize();
		int page = pageable.getPageNumber();

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add(kakaoProperties.getParamX(), x);
		params.add(kakaoProperties.getParamY(), y);
		params.add(kakaoProperties.getParamRadius(), kakaoProperties.getRadius());
		params.add(kakaoProperties.getParamSize(), String.valueOf(size));
		params.add(kakaoProperties.getParamPage(), String.valueOf(page));
		params.add(kakaoProperties.getParamGroup(), PlaceStandard.CAFE.getCode());

		String url = kakaoProperties.getCategorySearchUrl();

		return webClientUtil.getKakao(url, params, KakaoSearchResponse.class);
	}

	//Study 가져오기
	private KakaoSearchResponse getKaKaoForStudy(String x, String y, Pageable pageable) {
		int size = pageable.getPageSize();
		int page = pageable.getPageNumber();

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add(kakaoProperties.getParamX(), x);
		params.add(kakaoProperties.getParamY(), y);
		params.add(kakaoProperties.getParamRadius(), kakaoProperties.getRadius());
		params.add(kakaoProperties.getParamSize(), String.valueOf(size));
		params.add(kakaoProperties.getParamPage(), String.valueOf(page));

		params.add(kakaoProperties.getParamQuery(), URLEncoder.encode("스터디", UTF_8));

		String url = kakaoProperties.getKeywordSearchUrl();

		return webClientUtil.getKakao(url, params, KakaoSearchResponse.class);
	}

	//Restaurant 가져오기
	private KakaoSearchResponse getKaKaoForRestaurant(String x, String y, Pageable pageable) {
		int size = pageable.getPageSize();
		int page = pageable.getPageNumber();

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add(kakaoProperties.getParamX(), x);
		params.add(kakaoProperties.getParamY(), y);
		params.add(kakaoProperties.getParamRadius(), kakaoProperties.getRadius());
		params.add(kakaoProperties.getParamSize(), String.valueOf(size));
		params.add(kakaoProperties.getParamPage(), String.valueOf(page));
		params.add(kakaoProperties.getParamGroup(), PlaceStandard.RESTAURANT.getCode());

		String url = kakaoProperties.getCategorySearchUrl();

		return webClientUtil.getKakao(url, params, KakaoSearchResponse.class);
	}
}
