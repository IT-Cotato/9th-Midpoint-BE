package middle_point_search.backend.common.exception.errorCode;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
	HttpStatus getHttpStatus();
	String getCode();
	String getMessage();
}
