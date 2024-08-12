package middle_point_search.backend.domains.recommendPlace.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.domains.market.domain.PlaceStandard;

@Slf4j
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecommendPlacesDto {

	@Setter
	private int pageableCount;

	@Setter
	private int totalCount;

	List<RecommendPlacesFindResponse> recommendPlaces;

	public static RecommendPlacesDto of(KakaoSearchResponse kakaoSearchResponse, PlaceStandard placeStandard) {
		int pageableCount = kakaoSearchResponse.getMeta().getPageable_count();
		int totalCount = kakaoSearchResponse.getMeta().getTotal_count();

		List<RecommendPlacesFindResponse> recommendPlacesFindResponses = kakaoSearchResponse
			.getDocuments()
			.stream()
			.map(document -> RecommendPlacesFindResponse.from(document, placeStandard))
			.collect(Collectors.toList());

		return new RecommendPlacesDto(pageableCount, totalCount, recommendPlacesFindResponses);
	}
}
