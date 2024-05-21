package middle_point_search.backend.common.dto;

import org.springframework.http.HttpStatusCode;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse extends BaseResponse {
	private final String error;

	private ErrorResponse(boolean isSuccess, HttpStatusCode status, String error) {
		super(isSuccess, status);
		this.error = error;
	}

	public ErrorResponse create(boolean isSuccess, HttpStatusCode status, String error) {
		return new ErrorResponse(isSuccess, status, error);
	}

}
