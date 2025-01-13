package middle_point_search.backend.domains.place.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import middle_point_search.backend.domains.place.domain.Place;

@Getter
@AllArgsConstructor
public class FindPlacesResponse {

	private final Boolean myLocationExistence;
	private final List<PlaceVO> myLocations;
	private final Boolean friendLocationExistence;
	private final List<PlaceVO> friendLocations;

	@Getter
	@AllArgsConstructor
	public static class PlaceVO {
		private final Long placeId;
		private final String siDo;
		private final String siGunGu;
		private final String roadNameAddress;
		private final Double addressLat;
		private final Double addressLong;

		public static PlaceVO from(Place place) {
			return new PlaceVO(
				place.getId(),
				place.getSiDo(),
				place.getSiGunGu(),
				place.getRoadNameAddress(),
				place.getAddressLatitude(),
				place.getAddressLongitude()
			);
		}
	}
}

