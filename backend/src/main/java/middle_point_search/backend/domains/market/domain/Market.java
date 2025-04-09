package middle_point_search.backend.domains.market.domain;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
	private String roadNameAddress;

	@Column(nullable = false)
	private Double addressLatitude;

	@Column(nullable = false)
	private Double addressLongitude;

	@Builder
	public Market(
		String name,
		String siDo,
		String siGunGu,
		String roadNameAddress,
		Double addressLatitude,
		Double addressLongitude
	) {
		this.name = name;
		this.siDo = siDo;
		this.siGunGu = siGunGu;
		this.roadNameAddress = roadNameAddress;
		this.addressLatitude = addressLatitude;
		this.addressLongitude = addressLongitude;
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
