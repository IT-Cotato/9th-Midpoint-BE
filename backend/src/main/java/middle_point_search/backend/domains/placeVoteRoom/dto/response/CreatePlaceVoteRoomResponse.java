package middle_point_search.backend.domains.placeVoteRoom.dto.response;

public record CreatePlaceVoteRoomResponse(Long id) {
	public static CreatePlaceVoteRoomResponse from(Long id) {
		return new CreatePlaceVoteRoomResponse(id);
	}
}
