package middle_point_search.backend.domains.member.dto.request;

public record LoginMemberRequest(
	String email,
	String pw
) {
}
