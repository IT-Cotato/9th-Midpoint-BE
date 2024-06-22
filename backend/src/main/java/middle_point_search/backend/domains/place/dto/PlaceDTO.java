package middle_point_search.backend.domains.place.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import middle_point_search.backend.domains.member.domain.Transport;

public class PlaceDTO {

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class PlaceSaveRequest {
		private final String placeRoomId;
		private final String siDo;
		private final String siGunGu;
		private final String roadNameAddress;
		private final Double addressLat;
		private final Double addressLong;
		private final Transport transport;
	}
}
