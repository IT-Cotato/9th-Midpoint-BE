package middle_point_search.backend.common.security.login.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {

	private String placeRoomId;
	private String name;
	private String pw;
}
