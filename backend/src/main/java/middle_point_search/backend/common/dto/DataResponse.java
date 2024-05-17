package middle_point_search.backend.common.dto;

import org.springframework.http.HttpStatusCode;

public class DataResponse extends BaseResponse{
	protected DataResponse(boolean isSuccess, HttpStatusCode status) {
		super(isSuccess, status);
	}
}
