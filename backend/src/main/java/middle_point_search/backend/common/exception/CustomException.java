package middle_point_search.backend.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import middle_point_search.backend.common.exception.errorCode.ErrorCode;

@Getter
public class CustomException extends RuntimeException {

	private final HttpStatus httpStatus;

	private final String code;

	private CustomException(HttpStatus httpStatus, String code, String message) {
		super(message);
		this.httpStatus = httpStatus;
		this.code = code;
	}

	public static CustomException from(ErrorCode errorCode) {
		return new CustomException(errorCode.getHttpStatus(), errorCode.getCode(), errorCode.getMessage());
	}
}

