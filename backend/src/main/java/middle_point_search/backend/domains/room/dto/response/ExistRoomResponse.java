package middle_point_search.backend.domains.room.dto.response;

public record ExistRoomResponse(
	boolean existence
) {
	public static ExistRoomResponse from(boolean existence) {
		return new ExistRoomResponse(existence);
	}
}