package middle_point_search.backend.domains.memberRoom.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import middle_point_search.backend.domains.room.domain.Room;

public class MemberRoomDTO {

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class MemberToRoomSaveRequest {

		@NotNull(message = "roomId는 필수값입니다.")
		private Long roomId;
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class FindRoomsByMemberIdResponse {
		private String roomId;
		private String roomName;

		public static FindRoomsByMemberIdResponse from(Room room) {
			return new FindRoomsByMemberIdResponse(room.getId(), room.getName());
		}
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class ExistsMemberRoomResponse {
		private Boolean exists;

		public static ExistsMemberRoomResponse from(Boolean exists) {
			return new ExistsMemberRoomResponse(exists);
		}
	}
}
