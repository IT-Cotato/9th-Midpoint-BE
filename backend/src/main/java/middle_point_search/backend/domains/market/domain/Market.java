package middle_point_search.backend.domains.market.domain;

import java.util.Objects;

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
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
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

	public static Market from(MarketApiData marketApiData) {
		String name = parseName(marketApiData.getName());
		String siGunGu = marketApiData.getSiGunGu();
		String siDo = marketApiData.getSiDo();
		Double addressLatitude = parseCoordinatesToLatitude(marketApiData.getCoordinates());
		Double addressLongitude = parseCoordinatesToLongitude(marketApiData.getCoordinates());

		return new Market(name, siGunGu, siDo, addressLatitude, addressLongitude);
	}

	private static String parseName(String name) {
		String[] s = name.split("_");

		return s[0];
	}

	private static Double parseCoordinatesToLatitude(String coordinates) {
		String coordinate = coordinates.split("\\|")[0];
		String[] coordinateParts = coordinate.split(",");

		return Double.parseDouble(coordinateParts[1]);
	}

	private static Double parseCoordinatesToLongitude(String coordinates) {
		String coordinate = coordinates.split("\\|")[0];
		String[] coordinateParts = coordinate.split(",");

		return Double.parseDouble(coordinateParts[0]);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Market market = (Market) o;
		return Objects.equals(name, market.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
