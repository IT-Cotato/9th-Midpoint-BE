package middle_point_search.backend.common.security.login.handler;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.dto.DataResponse;
import middle_point_search.backend.common.dto.ErrorResponse;
import middle_point_search.backend.common.security.exception.RoomNotFoundException;
import middle_point_search.backend.common.util.ResponseWriter;

@Component
@RequiredArgsConstructor
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) {
		if (exception instanceof RoomNotFoundException) {
			ErrorResponse errorResponse = ErrorResponse.from(ROOM_NOT_FOUND);
			ResponseWriter.writeResponse(response, errorResponse, ROOM_NOT_FOUND.getHttpStatus());
		} else {
			ResponseWriter.writeResponse(response, DataResponse.ok(), HttpStatus.UNAUTHORIZED);
		}
	}
}