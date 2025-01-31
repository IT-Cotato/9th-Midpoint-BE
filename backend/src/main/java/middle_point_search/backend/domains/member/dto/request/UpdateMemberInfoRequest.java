package middle_point_search.backend.domains.member.dto.request;

public record UpdateMemberInfoRequest(
	String name,
	String siDo,
	String siGunGu,
	String roadNameAddress,
	Double addressLatitude,
	Double addressLongitude
) {
}
