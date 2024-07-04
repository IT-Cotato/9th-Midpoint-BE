package middle_point_search.backend.domains.market.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import middle_point_search.backend.domains.market.domain.PlaceStandard;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecommendPlacesFindRequest {

	@NotNull
	private Double addressLat;
	@NotNull
	private Double addressLong;
	@NotNull
	private PlaceStandard placeStandard;
	@NotNull
	@Positive
	private Integer page;
}
