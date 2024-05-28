package middle_point_search.backend.common.exception.errorCode;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum CommonErrorCode implements ErrorCode {

	INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청 자원이 존재하지 않습니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부에서 에러가 발생하였습니다."),
	DATA_BUFFER_LIMIT_ERROR(HttpStatus.PAYLOAD_TOO_LARGE, "요청의 페이로드가 너무 큽니다."),
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
