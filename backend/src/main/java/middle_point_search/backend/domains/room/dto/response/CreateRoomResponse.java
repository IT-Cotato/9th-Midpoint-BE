package middle_point_search.backend.domains.room.dto.response;

public record CreateRoomResponse(
	String id
) {
	public static CreateRoomResponse from(String id) {
		return new CreateRoomResponse(id);
	}
}
