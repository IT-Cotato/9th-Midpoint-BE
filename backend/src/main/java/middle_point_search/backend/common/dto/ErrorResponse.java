package middle_point_search.backend.common.dto;

import org.springframework.http.HttpStatusCode;

public class ErrorResponse extends BaseResponse{
	protected ErrorResponse(boolean isSuccess, HttpStatusCode status) {
		super(isSuccess, status);
	}
}
