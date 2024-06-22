package middle_point_search.backend.domains.place.dto;

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
}
