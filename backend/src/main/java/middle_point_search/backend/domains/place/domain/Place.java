package middle_point_search.backend.domains.place.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import middle_point_search.backend.domains.member.domain.Transport;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Place {

	@Id
	@Column(name = "PLACE_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NonNull
	@Enumerated(EnumType.STRING)
	private Transport transport;

	@NonNull
	@Column(nullable = false)
	private String siDo;

	@NonNull
	@Column(nullable = false)
	private String siGunGu;

	@NonNull
	@Column(nullable = false)
	private String roadNameAddress;

	@NonNull
	@Column(nullable = false)
	private Double addressLatitude;

	@NonNull
	@Column(nullable = false)
	private Double addressLongitude;

	public static Place of(Transport transport, String siDo, String siGunGu, String roadNameAddress,
		Double addressLatitude, Double addressLongitude) {
		return new Place(transport, siDo, siGunGu, roadNameAddress, addressLatitude, addressLongitude);
	}
}
