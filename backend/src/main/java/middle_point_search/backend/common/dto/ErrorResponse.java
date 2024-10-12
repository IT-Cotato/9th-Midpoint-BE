package middle_point_search.backend.common.dto;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.common.exception.errorCode.ErrorCode;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse extends BaseResponse {
	private final String error;
	private final String code;
	private final List<String> reason;

	private ErrorResponse(Boolean isSuccess, HttpStatusCode status, String code, String error) {
		super(isSuccess, status);
		this.error = error;
		this.code = code;
		this.reason = null;
	}

	public ErrorResponse(Boolean isSuccess, HttpStatusCode status, String code, String error, List<String> reason) {
		super(isSuccess, status);
		this.error = error;
		this.code = code;
		this.reason = reason;
	}

	public static ErrorResponse from(CustomException customException) {
		Boolean isSuccess = false;
		HttpStatus status = customException.getHttpStatus();
		String code = status.toString();
		String error = customException.getMessage();

		return new ErrorResponse(isSuccess, status, code, error);
	}

	public static ErrorResponse of(ErrorCode errorCode, List<String> reason) {
		Boolean isSuccess = false;
		HttpStatus status = errorCode.getHttpStatus();
		String code = errorCode.getCode();
		String error = errorCode.getMessage();

		return new ErrorResponse(isSuccess, status, code, error, reason);
	}

	public static ErrorResponse from(ErrorCode errorCode) {
		Boolean isSuccess = false;
		HttpStatus status = errorCode.getHttpStatus();
		String code = errorCode.getCode();
		String error = errorCode.getMessage();

		return new ErrorResponse(isSuccess, status, code, error);
	}
}
