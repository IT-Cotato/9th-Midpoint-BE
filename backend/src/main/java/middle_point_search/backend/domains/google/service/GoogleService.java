package middle_point_search.backend.domains.google.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.common.exception.errorCode.UserErrorCode;
import middle_point_search.backend.common.properties.GoogleProperties;
import middle_point_search.backend.common.webClient.util.WebClientUtil;
import middle_point_search.backend.domains.google.dto.GoogleApiResponse;
import middle_point_search.backend.domains.google.dto.ReverseGeocodeResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoogleService {

	private final WebClientUtil webClientUtil;
	private final GoogleProperties googleProperties;

	// 구글 placeId 찾기
	public String findGooglePlaceId(Double latitude, Double longitude) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("latlng", latitude + "," + longitude);
		params.add("language", "ko");

		ReverseGeocodeResponse response = webClientUtil.getGoogle(
			googleProperties.getMap().getPlaceIdUrl(),
			params,
			ReverseGeocodeResponse.class
		);

		// 상태코드 체크
		checkGoogleApiResponseStatus(response);

		return response.getResults().get(0).getPlace_id();
	}

	// 상태코드 확인
	public void checkGoogleApiResponseStatus(Object response) {
		GoogleApiResponse googleApiResponse = (GoogleApiResponse)response;
		if (!googleApiResponse.getStatus().equals("OK")) {
			throw CustomException.from(UserErrorCode.API_INTERNAL_SERVER_ERROR);
		}
	}
}
