package middle_point_search.backend.domains.timeVoteRoom.dto.response;

public record CreateTimeVoteRoomResponse(
	Long id
) {
	public static CreateTimeVoteRoomResponse from(Long id) {
		return new CreateTimeVoteRoomResponse(id);
	}
}
