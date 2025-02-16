package middle_point_search.backend.domains.member.dto.response;

public record FindProfileImageUrlResponse(
	boolean isExist,
	String url
) {
}
