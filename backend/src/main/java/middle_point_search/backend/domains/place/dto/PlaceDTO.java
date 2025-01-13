package middle_point_search.backend.domains.place.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class PlaceDTO {


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
