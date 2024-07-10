package middle_point_search.backend.domains.market.dto.response;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecommendPlacesDto {

	@Setter
	int pageableCount;
	List<RecommendPlacesFindResponse> recommendPlaces;

	public static RecommendPlacesDto from(int pageableCount, List<RecommendPlacesFindResponse> recommendPlacesFindResponses) {
		return new RecommendPlacesDto(pageableCount, recommendPlacesFindResponses);
	}
}
