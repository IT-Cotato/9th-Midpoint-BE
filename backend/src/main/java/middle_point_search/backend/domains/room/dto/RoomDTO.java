package middle_point_search.backend.domains.room.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class RoomDTO {

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class RoomCreateResponse {
		private final String id;

		public static RoomCreateResponse from(String id) {
			return new RoomCreateResponse(id);
		}
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class RoomExistenceCheckResponse {
		private Boolean existence;

		public static RoomExistenceCheckResponse from(Boolean existence) {
			return new RoomExistenceCheckResponse(existence);
		}
	}
}
