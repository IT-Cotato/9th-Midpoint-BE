package middle_point_search.backend.domains.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginMemberRequest {
	private String email;
	private String pw;
}
