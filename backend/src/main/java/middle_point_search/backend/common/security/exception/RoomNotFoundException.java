package middle_point_search.backend.common.security.exception;

import org.springframework.security.core.AuthenticationException;

public class RoomNotFoundException extends AuthenticationException {
	public RoomNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public RoomNotFoundException(String msg) {
		super(msg);
	}
}