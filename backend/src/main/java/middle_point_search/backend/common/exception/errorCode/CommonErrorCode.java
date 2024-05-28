package middle_point_search.backend.common.exception.errorCode;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum CommonErrorCode implements ErrorCode {

	INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "Invalid parameter included"),
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not exists"),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
	DATA_BUFFER_LIMIT_ERROR(HttpStatus.PAYLOAD_TOO_LARGE, "Data buffer limit error"),
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
