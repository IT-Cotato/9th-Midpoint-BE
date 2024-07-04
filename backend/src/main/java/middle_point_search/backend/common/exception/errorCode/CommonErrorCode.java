package middle_point_search.backend.common.exception.errorCode;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum CommonErrorCode implements ErrorCode {

	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증에 실패하였습니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "접근이 거부되었습니다."),
	BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
	EXTERNAL_SERVER_ERROR(HttpStatus.BAD_REQUEST, "외부 서버로의 요청이 실패하였습니다."),
	INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "요청 파라미터가 잘 못 되었습니다."),
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부에서 에러가 발생하였습니다."),
	DATA_BUFFER_LIMIT_ERROR(HttpStatus.PAYLOAD_TOO_LARGE, "요청의 페이로드가 너무 큽니다."),
	AUTHENTICATION_SERVICE_ERROR(HttpStatus.BAD_REQUEST, "요청한 content-type은 허락되지 않습니다."),
	;

	private final HttpStatus httpStatus;
	private final String message;

	CommonErrorCode(HttpStatus httpStatus, String message) {
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
