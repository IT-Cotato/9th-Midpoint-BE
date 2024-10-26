package middle_point_search.backend.domains.place.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PlaceDTO {

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class PlaceSaveOrUpdateRequest {

		@NotBlank(message = "siDo는 비어 있을 수 없습니다.")
		private String siDo;
		@NotBlank(message = "siGunGu는 비어 있을 수 없습니다.")
		private String siGunGu;
		@NotBlank(message = "roadNameAddress는 비어 있을 수 없습니다.")
		private String roadNameAddress;
		@NotNull
		@Positive(message = "addreesLat은 양수이어야 합니다.")
		private Double addressLat;
		@NotNull
		@Positive(message = "addreesLong은 양수이어야 합니다.")
		private Double addressLong;
	}

	@Getter
	@AllArgsConstructor
	public static class PlacesFindResponse {

		private final Boolean existence;
		private final List<PlaceVO> places;
	}

	@Getter
	@AllArgsConstructor
	public static class PlaceVO {
		private final Long placeId;
		private final String siDo;
		private final String siGunGu;
		private final String roadNameAddress;
		private final Double addressLat;
		private final Double addressLong;
	}
}
