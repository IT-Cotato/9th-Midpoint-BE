package middle_point_search.backend.domains.room.dto.response;

import java.util.List;

import middle_point_search.backend.domains.room.domain.Room;

public record FindRoomDetailResponse(
	String name,
	String memo,
	int memberCount,
	List<String> emails
) {
	public static FindRoomDetailResponse from(Room room, List<String> emails) {
		return new FindRoomDetailResponse(
			room.getName(),
			room.getMemo(),
			emails.size(),
			emails);
	}
}
