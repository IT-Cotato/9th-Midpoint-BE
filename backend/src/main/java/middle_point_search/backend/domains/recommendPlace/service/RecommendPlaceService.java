package middle_point_search.backend.domains.recommendPlace.service;

import static java.nio.charset.StandardCharsets.*;

import java.net.URLEncoder;
import java.util.Collections;

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
import middle_point_search.backend.common.webClient.util.WebClientUtil;
import middle_point_search.backend.domains.market.domain.PlaceStandard;
import middle_point_search.backend.domains.recommendPlace.dto.KakaoRequestDTO;
import middle_point_search.backend.domains.recommendPlace.dto.request.RecommendPlacesFindRequest;
import middle_point_search.backend.domains.recommendPlace.dto.response.KakaoSearchResponse;
import middle_point_search.backend.domains.recommendPlace.dto.response.RecommendPlacesDto;
import middle_point_search.backend.domains.recommendPlace.dto.response.RecommendPlacesFindResponse;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecommendPlaceService {

	private final KakaoProperties kakaoProperties;

	private final WebClientUtil webClientUtil;

	// 키워드로 주위 장소 조회
	public Page<RecommendPlacesFindResponse> findRecommendPlaces(RecommendPlacesFindRequest request) {
		String x = request.getAddressLong().toString();
		String y = request.getAddressLat().toString();
		int page = request.getPage();
		int size = kakaoProperties.getSize();

		KakaoRequestDTO kakaoRequestDTO = new KakaoRequestDTO(x, y, page + 1, size); //kakao의 page는 1부터 시작
		Pageable pageable = PageRequest.of(page, size); //java의 page는 0부터 시작

		RecommendPlacesDto response = checkPlaceStandardAndGetResponse(request, kakaoRequestDTO);

		log.info("data count : {}", response.getRecommendPlaces().size());

		int totalCount = response.getPageableCount();

		log.info("totalCount : {}", totalCount);

		PageImpl<RecommendPlacesFindResponse> responses = new PageImpl<>(
			response.getRecommendPlaces(), pageable, totalCount);

		log.info("offset : {}", responses.getPageable().getOffset());
		log.info("totalCount : {}", responses.getTotalElements());
		log.info("totalPage : {}", responses.getTotalPages());

		return responses;
	}

	//PlaceStandard에 따라 KakaoSearchResponse를 가져오는 메서드
	private RecommendPlacesDto checkPlaceStandardAndGetResponse(
		RecommendPlacesFindRequest request,
		KakaoRequestDTO kakaoRequestDTO) {

		if (request.getPlaceStandard() == PlaceStandard.ALL) {
			return getKaKaoForAll(kakaoRequestDTO);
		} else if (request.getPlaceStandard() == PlaceStandard.STUDY) {
			return getKaKaoForStudy(kakaoRequestDTO);
		} else if (request.getPlaceStandard() == PlaceStandard.CAFE) {
			return getKaKaoForCafe(kakaoRequestDTO);
		} else if (request.getPlaceStandard() == PlaceStandard.RESTAURANT) {
			return getKaKaoForRestaurant(kakaoRequestDTO);
		}

		throw new CustomException(CommonErrorCode.INVALID_PARAMETER);
	}

	//Cafe, Study, Restaurant 한번에 가져오기
	private RecommendPlacesDto getKaKaoForAll(KakaoRequestDTO kakaoRequestDTO) {
		String x = kakaoRequestDTO.x();
		String y = kakaoRequestDTO.y();
		int size = kakaoRequestDTO.size();
		int page = kakaoRequestDTO.page();

		int cafeSize = size / 3;
		int restaurantSize = size - size / 3 * 2;
		int studySize = cafeSize;

		RecommendPlacesDto response1 = getKaKaoForStudy(new KakaoRequestDTO(x, y, page, studySize));
		RecommendPlacesDto response2 = getKaKaoForCafe(new KakaoRequestDTO(x, y, page, cafeSize));
		RecommendPlacesDto response3 = getKaKaoForRestaurant(new KakaoRequestDTO(x, y, page, restaurantSize));

		//위 셋의 응답을 response1로 합치기
		int count = response1.getPageableCount();
		count += response2.getPageableCount();
		count += response3.getPageableCount();

		response1.setPageableCount(count);

		response1.getRecommendPlaces().addAll(response2.getRecommendPlaces());
		response1.getRecommendPlaces().addAll(response3.getRecommendPlaces());

		Collections.sort(response1.getRecommendPlaces());

		return response1;
	}

	//Cafe 가져오기
	private RecommendPlacesDto getKaKaoForCafe(KakaoRequestDTO kakaoRequestDTO) {
		String x = kakaoRequestDTO.x();
		String y = kakaoRequestDTO.y();
		int size = kakaoRequestDTO.size();
		int page = kakaoRequestDTO.page();

		MultiValueMap<String, String> params = makeBaseParams(x, y, size, page);
		params.add(kakaoProperties.getParamGroup(), PlaceStandard.CAFE.getCode());

		String url = kakaoProperties.getCategorySearchUrl();

		KakaoSearchResponse kakaoSearchResponse = webClientUtil.getKakao(url, params, KakaoSearchResponse.class);

		return RecommendPlacesDto.of(kakaoSearchResponse, PlaceStandard.CAFE);
	}

	//Study 가져오기
	private RecommendPlacesDto getKaKaoForStudy(KakaoRequestDTO kakaoRequestDTO) {
		String x = kakaoRequestDTO.x();
		String y = kakaoRequestDTO.y();
		int size = kakaoRequestDTO.size();
		int page = kakaoRequestDTO.page();

		MultiValueMap<String, String> params = makeBaseParams(x, y, size, page);
		params.add(kakaoProperties.getParamQuery(), URLEncoder.encode("스터디", UTF_8));

		String url = kakaoProperties.getKeywordSearchUrl();

		KakaoSearchResponse kakaoSearchResponse = webClientUtil.getKakao(url, params, KakaoSearchResponse.class);

		return RecommendPlacesDto.of(kakaoSearchResponse, PlaceStandard.STUDY);
	}

	//Restaurant 가져오기
	private RecommendPlacesDto getKaKaoForRestaurant(KakaoRequestDTO kakaoRequestDTO) {
		String x = kakaoRequestDTO.x();
		String y = kakaoRequestDTO.y();
		int size = kakaoRequestDTO.size();
		int page = kakaoRequestDTO.page();

		MultiValueMap<String, String> params = makeBaseParams(x, y, size, page);
		params.add(kakaoProperties.getParamGroup(), PlaceStandard.RESTAURANT.getCode());

		String url = kakaoProperties.getCategorySearchUrl();

		KakaoSearchResponse kakaoSearchResponse = webClientUtil.getKakao(url, params, KakaoSearchResponse.class);

		return RecommendPlacesDto.of(kakaoSearchResponse, PlaceStandard.RESTAURANT);
	}

	//기본적인 param을 만들어주는 메서드
	private MultiValueMap<String, String> makeBaseParams(String x, String y, int size, int page) {

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add(kakaoProperties.getParamX(), x);
		params.add(kakaoProperties.getParamY(), y);
		params.add(kakaoProperties.getParamRadius(), kakaoProperties.getRadius());
		params.add(kakaoProperties.getParamSize(), String.valueOf(size));
		params.add(kakaoProperties.getParamPage(), String.valueOf(page));
		return params;
	}
}
