package middle_point_search.backend.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import middle_point_search.backend.common.exception.errorCode.ErrorCode;

@Getter
public class CustomException extends RuntimeException {

	private final HttpStatus httpStatus;

	private CustomException(HttpStatus httpStatus, String message) {
		super(message);
		this.httpStatus = httpStatus;
	}

	public static CustomException from(ErrorCode errorCode) {
		return new CustomException(errorCode.getHttpStatus(), errorCode.getMessage());
	}

	public static CustomException of(HttpStatus httpStatus, String message) {
		return new CustomException(httpStatus, message);
	}
}

