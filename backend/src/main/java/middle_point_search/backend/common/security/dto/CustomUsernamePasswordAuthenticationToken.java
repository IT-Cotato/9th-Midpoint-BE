package middle_point_search.backend.common.security.dto;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import lombok.Getter;

@Getter
public class CustomUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {

	private final String placeRoomId;

	private CustomUsernamePasswordAuthenticationToken(Object principal, Object credentials, String placeRoomId) {
		super(principal, credentials);
		this.placeRoomId = placeRoomId;
	}

	public static CustomUsernamePasswordAuthenticationToken from(CustomUserDetails customUserDetails) {
		String principal = customUserDetails.getUsername();
		String credentials = customUserDetails.getPassword();
		String roomId = customUserDetails.getRoomId();

		return new CustomUsernamePasswordAuthenticationToken(principal, credentials, roomId);
	}

	public static CustomUsernamePasswordAuthenticationToken of(Object principal, Object credentials, String roomId) {
		return new CustomUsernamePasswordAuthenticationToken(principal, credentials, roomId);
	}
}
