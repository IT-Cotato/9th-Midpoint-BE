package middle_point_search.backend.domains.member.dto.request;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LoginMemberRequest {
	private String email;
	private String pw;
}
