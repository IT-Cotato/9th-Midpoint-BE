package middle_point_search.backend.domains.recommendPlace.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import middle_point_search.backend.domains.market.domain.PlaceStandard;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecommendPlacesFindRequest {

	@Parameter(required = true)
	@NotNull(message = "값이 비어있으면 안 됩니다.")
	@Positive(message = "addreesLat은 양수이어야 합니다.")
	private Double addressLat;

	@Parameter(required = true)
	@NotNull(message = "값이 비어있으면 안 됩니다.")
	@Positive(message = "addreesLong은 양수이어야 합니다.")
	private Double addressLong;

	@Parameter(required = true)
	@NotNull(message = "값이 비어있으면 안 됩니다.")
	private PlaceStandard placeStandard;

	@NotNull(message = "값이 비어있으면 안 됩니다.")
	@Min(value = 1, message = "page는 1이상이어야 합니다.")
	@Max(value = 27, message = "page는 27이하이어야 합니다.")
	private Integer page;
}
