package middle_point_search.backend.common.exception.errorCode;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum CommonErrorCode implements ErrorCode {

	// 1XX
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C-101", "인증에 실패하였습니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "C-102", "접근이 거부되었습니다."),
	AUTHENTICATION_SERVICE_ERROR(HttpStatus.BAD_REQUEST, "C-103", "요청한 content-type은 허락되지 않습니다."),

	// 2XX
	BAD_REQUEST(HttpStatus.BAD_REQUEST, "C-201", "잘못된 요청입니다."),
	INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "C-202", "요청 파라미터가 잘 못 되었습니다."),
	TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "C-203", "요청을 너무 많이 했습니다."),
	DATA_BUFFER_LIMIT_ERROR(HttpStatus.PAYLOAD_TOO_LARGE, "C-204", "요청의 페이로드가 너무 큽니다."),

	// 3XX
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C-301", "리소스를 찾을 수 없습니다."),

	// 4XX
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C-401", "서버 내부에서 에러가 발생하였습니다."),
	EXTERNAL_SERVER_ERROR(HttpStatus.BAD_REQUEST, "C-402", "외부 서버로의 요청이 실패하였습니다.");
	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	CommonErrorCode(HttpStatus httpStatus, String code, String message) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.message = message;
	}

	@Override
	public HttpStatus getHttpStatus() {
		return this.httpStatus;
	}

	@Override
	public String getCode() {
		return this.code;
	}

	@Override
	public String getMessage() {
		return this.message;
	}
}
