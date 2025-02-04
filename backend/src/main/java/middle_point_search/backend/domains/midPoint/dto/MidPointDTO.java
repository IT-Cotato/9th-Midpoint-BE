package middle_point_search.backend.domains.midPoint.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import middle_point_search.backend.domains.market.domain.Market;
import middle_point_search.backend.domains.place.domain.Place;

public class MidPointDTO {

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class AddressDTO {

		private String siDo;
		private String siGunGu;
		private String roadNameAddress;
		private Double addressLat;
		private Double addressLong;

		public static AddressDTO from(Place place) {
			return new AddressDTO(
				place.getSiDo(),
				place.getSiGunGu(),
				place.getRoadNameAddress(),
				place.getAddressLatitude(),
				place.getAddressLongitude()
			);
		}

	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PUBLIC)
	public static class CoordinateDTO {

		private Double x;
		private Double y;

		// 두 점 사이의 거리 제곱을 리턴하는 함수
		public Double calcDist(CoordinateDTO b) {
			return Math.pow(this.y - b.getY(), 2D) + Math.pow(this.x - b.getX(), 2D);
		}
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PUBLIC)
	public static class FindMidPointsResponse {

		private String name;
		private String siDo;
		private String siGunGu;
		private String roadNameAddress;
		private Double addressLat;
		private Double addressLong;

		public static FindMidPointsResponse from(Market market) {
			return new FindMidPointsResponse(
				market.getName(),
				market.getSiDo(),
				market.getSiGunGu(),
				null,
				market.getAddressLatitude(),
				market.getAddressLongitude()
			);
		}
	}

	@Getter
	@AllArgsConstructor
	public static class FindTravelTimesResponse {
		private List<Element> elements;

		@Getter
		@AllArgsConstructor
		public static class Element {
			private String status;
			private Long placeId;
			private Duration duration;
			private Distance distance;

			@Getter
			@AllArgsConstructor(access = AccessLevel.PRIVATE)
			public static class Duration {
				private String text;
				private int value;
			}

			@Getter
			@AllArgsConstructor(access = AccessLevel.PRIVATE)
			public static class Distance {
				private String text;
				private int value;
			}

			public static Element from(
				Long placeId,
				String durationText,
				int durationValue,
				String distanceText,
				int distanceValue
			) {
				return new Element(
					"OK",
					placeId,
					new Element.Duration(durationText, durationValue),
					new Element.Distance(distanceText, distanceValue));
			}

			public static Element noContent(Long placeId) {
				return new Element(
					"ZERO_RESULTS",
					placeId,
					null,
					null);
			}
		}

		public static FindTravelTimesResponse from(List<Element> elements) {
			return new FindTravelTimesResponse(elements);
		}
	}
}
