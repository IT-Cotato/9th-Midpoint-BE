package middle_point_search.backend.common.dummy.dto;

public record PlaceVoteCandidateDummyDto(
	String name,
	String siDo,
	String siGunGu,
	String roadNameAddress,
	Double addressLatitude,
	Double addressLongitude,
	Long placeVoteRoomId
) {
}
