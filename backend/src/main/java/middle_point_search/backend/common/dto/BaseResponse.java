package middle_point_search.backend.common.dto;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse {
	@JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
	private final LocalDateTime timestamp = LocalDateTime.now();
	private final Boolean isSuccess;
	private final HttpStatusCode status;

	protected BaseResponse(Boolean isSuccess, HttpStatusCode status) {
		this.isSuccess = isSuccess;
		this.status = status;
	}

	public static BaseResponse ok() {
		boolean isSuccess = true;
		HttpStatusCode status = HttpStatus.OK;

		return new BaseResponse(isSuccess, status);
	}
}
