package middle_point_search.backend.common.dummy.dto;

public record PlaceDummyDto(
	String siDo,
	String siGunGu,
	String roadNameAddress,
	Double addressLatitude,
	Double addressLongitude,
	String roomId,
	Long memberId,
	String googlePlaceId
) {
}
