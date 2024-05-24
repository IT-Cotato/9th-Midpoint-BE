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
	private Float address_latitude;

	@Column(nullable = false)
	private Float address_longitude;

	private Market(String name, String siGunGu, String siDo, Float address_latitude,
		Float address_longitude) {
		this.name = name;
		this.siGunGu = siGunGu;
		this.siDo = siDo;
		this.address_latitude = address_latitude;
		this.address_longitude = address_longitude;
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
		Float address_latitude = parseCoordinatesToLatitude(marketApiData.getCoordinates());
		Float address_longitude = parseCoordinatesToLongitude(marketApiData.getCoordinates());

		return new Market(name, siGunGu, siDo, address_latitude, address_longitude);
	}

	private static Float parseCoordinatesToLatitude(String coordinates) {
		String coordinate = coordinates.split("\\|")[0];
		String[] coordinateParts = coordinate.split(",");

		return Float.parseFloat(coordinateParts[1]);
	}

	private static Float parseCoordinatesToLongitude(String coordinates) {
		String coordinate = coordinates.split("/")[0];
		String[] coordinateParts = coordinate.split(",");

		return Float.parseFloat(coordinateParts[0]);
	}
}
