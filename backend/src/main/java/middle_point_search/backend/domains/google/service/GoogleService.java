package middle_point_search.backend.domains.google.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.common.exception.errorCode.UserErrorCode;
import middle_point_search.backend.common.properties.GoogleProperties;
import middle_point_search.backend.common.webClient.util.WebClientUtil;
import middle_point_search.backend.domains.google.dto.DistanceMatrixResponse;
import middle_point_search.backend.domains.google.dto.GoogleApiResponse;
import middle_point_search.backend.domains.google.dto.ReverseGeocodeResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoogleService {

	private final WebClientUtil webClientUtil;
	private final GoogleProperties googleProperties;

	// 이동 시간 조회
	public DistanceMatrixResponse findTravelTimes(String destPlaceId, List<String> originPlaceIds) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add(googleProperties.getMap().getOrigin(), makePlaceIdsQuery(originPlaceIds));
		params.add(googleProperties.getMap().getDestination(), makePlaceIdQuery(destPlaceId));
		params.add("language", "ko");
		params.add("mode", "transit");
		params.add("region", "KR");

		DistanceMatrixResponse response = webClientUtil.getGoogle(googleProperties.getMap().getDistanceMatrixUrl(),
			params, DistanceMatrixResponse.class);

		// 상태코드 체크
		checkGoogleApiResponseStatus(response);

		return response;
	}

	// id들을 |로 구분하여 query문을 만들어줌
	private String makePlaceIdsQuery(List<String> placeIds) {
		StringBuilder query = new StringBuilder();
		// coordinate 사이에 |를 넣어줌, 마지막에 | 없음
		for (int i = 0; i < placeIds.size(); i++) {
			query.append("place_id:").append(placeIds.get(i));
			if (i != placeIds.size() - 1) {
				query.append("|");
			}
		}
		return query.toString();
	}

	// 장소 ID 쿼리 파라미터 생성
	private String makePlaceIdQuery(String placeId) {
		return "place_id:" + placeId;
	}

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

	// 이동 경로 조회
	public Object findDirections(String destPlaceId, String originPlaceId) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("origin", "place_id:" + originPlaceId);
		params.add("destination", "place_id:" + destPlaceId);
		params.add("language", "ko");
		params.add("mode", "transit");
		params.add("region", "KR");

		Object response = webClientUtil.getGoogle(googleProperties.getMap().getDirectionUrl(),
			params, Object.class);

		return response;
	}
}
