package middle_point_search.backend.domains.member.dto.response;

public record LoginMemberResponse(
	String accessToken,
	String refreshToken
) {
}
