package middle_point_search.backend.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.errorCode.ErrorCode;

@Getter
public class CustomException extends RuntimeException {

	private final ErrorCode errorCode;

	public CustomException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}

