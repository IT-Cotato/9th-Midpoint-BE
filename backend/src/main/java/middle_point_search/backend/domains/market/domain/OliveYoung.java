package middle_point_search.backend.domains.market.domain;

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
public class OliveYoung {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private Double latitude;
	private Double longitude;

	private String address;

	@Builder
	public OliveYoung(String name, Double latitude, Double longitude, String address) {
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = address;
	}
}
