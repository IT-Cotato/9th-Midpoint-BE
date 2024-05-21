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

	private ErrorResponse(boolean isSuccess, HttpStatusCode status, String error) {
		super(isSuccess, status);
		this.error = error;
	}

	public static ErrorResponse of(HttpStatusCode status, String error) {
		boolean isSuccess = false;

		return new ErrorResponse(isSuccess, status, error);
	}

	public static ErrorResponse from(ErrorCode errorCode) {
		boolean isSuccess = false;
		HttpStatus status = errorCode.getHttpStatus();
		String error = errorCode.getMessage();

		return new ErrorResponse(isSuccess, status, error);
	}

}
