package middle_point_search.backend.domains.market.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import middle_point_search.backend.domains.market.domain.PlaceStandard;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecommendPlacesFindRequest {

	private Double addressLat;
	private Double addressLong;
	private PlaceStandard placeStandard;
	private Integer page;
}
