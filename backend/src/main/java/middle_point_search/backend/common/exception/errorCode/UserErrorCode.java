package middle_point_search.backend.common.exception.errorCode;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum UserErrorCode implements ErrorCode {

	//4xx
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Token이 유효하지 않습니다."),
	ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
	API_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "API 인증 정보가 정확하지 않습니다."),
	//5xx
	API_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "API 서버에 문제가 발생하였습니다.")

	;

	private final HttpStatus httpStatus;
	private final String message;

	UserErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

	@Override
	public HttpStatus getHttpStatus() {
		return this.httpStatus;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

}
