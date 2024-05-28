package middle_point_search.backend.common.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import middle_point_search.backend.common.exception.errorCode.ErrorCode;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse extends BaseResponse {
	private final String error;
	private final String path;

	private ErrorResponse(Boolean isSuccess, HttpStatusCode status, String error) {
		super(isSuccess, status);
		this.error = error;
		this.path = null;
	}

	public ErrorResponse(Boolean isSuccess, HttpStatusCode status, String error, String path) {
		super(isSuccess, status);
		this.error = error;
		this.path = path;
	}

	public static ErrorResponse of(HttpStatusCode status, String error) {
		Boolean isSuccess = false;

		return new ErrorResponse(isSuccess, status, error);
	}

	public static ErrorResponse of(ErrorCode errorCode, String path) {
		Boolean isSuccess = false;
		HttpStatus status = errorCode.getHttpStatus();
		String error = errorCode.getMessage();

		return new ErrorResponse(isSuccess, status, error, path);
	}

	public static ErrorResponse from(ErrorCode errorCode) {
		Boolean isSuccess = false;
		HttpStatus status = errorCode.getHttpStatus();
		String error = errorCode.getMessage();

		return new ErrorResponse(isSuccess, status, error);
	}



}
