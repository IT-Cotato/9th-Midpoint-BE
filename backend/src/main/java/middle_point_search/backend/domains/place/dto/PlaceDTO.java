package middle_point_search.backend.domains.place.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import middle_point_search.backend.domains.member.domain.Transport;
import middle_point_search.backend.domains.place.domain.Place;

public class PlaceDTO {

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class PlaceSaveRequest {

		private String siDo;
		private String siGunGu;
		private String roadNameAddress;
		private Double addressLat;
		private Double addressLong;
		private Transport transport;
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class PlacesSaveBySelfRequest {

		List<PlaceSaveRequest> addresses;
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class PlaceUpdateRequest {

		private String siDo;
		private String siGunGu;
		private String roadNameAddress;
		private Double addressLat;
		private Double addressLong;
		private Transport transport;
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class PlacesFindResponse {

		private final Long placeId;
		private final String siDo;
		private final String siGunGu;
		private final String roadNameAddress;
		private final Double addressLat;
		private final Double addressLong;
		private final Transport transport;

		public static PlacesFindResponse from(Place place) {

			Long placeId = place.getId();
			String siDo = place.getSiDo();
			String siGunGu = place.getSiGunGu();
			String roadNameAddress = place.getRoadNameAddress();
			Double addressLat = place.getAddressLatitude();
			Double addressLong = place.getAddressLongitude();
			Transport transport = place.getTransport();

			return new PlacesFindResponse(
				placeId,
				siDo,
				siGunGu,
				roadNameAddress,
				addressLat,
				addressLong,
				transport);
		}
	}
}
