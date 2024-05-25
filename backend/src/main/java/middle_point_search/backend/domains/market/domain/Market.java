package middle_point_search.backend.domains.market.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import middle_point_search.backend.domains.market.dto.response.MarketApiData;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Market {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "market_id")
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String siGunGu;

	@Column(nullable = false)
	private String siDo;

	@Column(nullable = false)
	private Double addressLatitude;

	@Column(nullable = false)
	private Double addressLongitude;

	private Market(String name, String siGunGu, String siDo, Double addressLatitude,
		Double addressLongitude) {
		this.name = name;
		this.siGunGu = siGunGu;
		this.siDo = siDo;
		this.addressLatitude = addressLatitude;
		this.addressLongitude = addressLongitude;
	}

	// public static Market of() {
	//
	// }
	//
	public static Market from(MarketApiData marketApiData) {
		System.out.println(marketApiData);

		String name = marketApiData.getName();
		String siGunGu = marketApiData.getSiGunGu();
		String siDo = marketApiData.getSiDo();
		Double addressLatitude = parseCoordinatesToLatitude(marketApiData.getCoordinates());
		Double addressLongitude = parseCoordinatesToLongitude(marketApiData.getCoordinates());

		return new Market(name, siGunGu, siDo, addressLatitude, addressLongitude);
	}

	private static Double parseCoordinatesToLatitude(String coordinates) {
		String coordinate = coordinates.split("|")[0];
		String[] coordinateParts = coordinate.split(",");

		return Double.parseDouble(coordinateParts[1]);
	}

	private static Double parseCoordinatesToLongitude(String coordinates) {
		String coordinate = coordinates.split("/")[0];
		String[] coordinateParts = coordinate.split(",");

		return Double.parseDouble(coordinateParts[0]);
	}
}
