package middle_point_search.backend.domains.midPoint.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import middle_point_search.backend.domains.market.domain.Market;
import middle_point_search.backend.domains.member.domain.Transport;
import middle_point_search.backend.domains.place.domain.Place;

public class MidPointDTO {

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class MidPointsBySelfFindRequest {
		List<AddressDTO> addresses;
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class AddressDTO {

		private String siDo;
		private String siGunGu;
		private String roadNameAddress;
		private Double addressLat;
		private Double addressLong;
		private Transport transport;

		public static AddressDTO from(Place place) {
			return new AddressDTO(
				place.getSiDo(),
				place.getSiGunGu(),
				place.getRoadNameAddress(),
				place.getAddressLatitude(),
				place.getAddressLongitude(),
				place.getTransport()
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
	public static class MidPointsFindResponse {

		private String name;
		private String siDo;
		private String siGunGu;
		private String roadNameAddress;
		private Double addressLat;
		private Double addressLong;

		public static MidPointsFindResponse from(Market market) {
			return new MidPointsFindResponse(
				market.getName(),
				market.getSiDo(),
				market.getSiGunGu(),
				null,
				market.getAddressLatitude(),
				market.getAddressLongitude()
			);
		}
	}
}
