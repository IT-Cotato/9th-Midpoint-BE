package middle_point_search.backend.common.exception.handler;

import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.dto.ErrorResponse;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.common.exception.errorCode.CommonErrorCode;
import middle_point_search.backend.common.exception.errorCode.ErrorCode;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<Object> handleCustomException(CustomException e) {
		log.warn("handleCustomException", e);

		return makeErrorResponseEntity(e.getErrorCode());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException e) {
		log.warn("handleIllegalArgument", e);

		ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
		return makeErrorResponseEntity(errorCode);
	}

	@Override
	protected ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex, HttpHeaders headers,
		HttpStatusCode status, WebRequest request) {
		log.warn("handleNoResourceFoundException", ex);

		ServletWebRequest servletWebRequest = (ServletWebRequest)request;
		String path = servletWebRequest.getRequest().getRequestURI();

		ErrorCode errorCode = CommonErrorCode.RESOURCE_NOT_FOUND;

		return makeErrorResponseEntity(errorCode, path);
	}

	@Override
	public ResponseEntity<Object> handleMethodArgumentNotValid(
		MethodArgumentNotValidException e,
		HttpHeaders headers,
		HttpStatusCode status,
		WebRequest request) {
		log.warn("handleIllegalArgument", e);

		ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
		return makeErrorResponseEntity(errorCode);
	}

	@ExceptionHandler(DataBufferLimitException.class)
	public ResponseEntity<Object> handleDataBufferLimitException(DataBufferLimitException e) {
		log.warn("handleDataBufferLimitException", e);

		CommonErrorCode errorCode = CommonErrorCode.DATA_BUFFER_LIMIT_ERROR;
		return makeErrorResponseEntity(errorCode);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
		HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		log.warn("handleHttpMessageNotReadableException", ex);

		ErrorCode errorCode = CommonErrorCode.BAD_REQUEST;
		return makeErrorResponseEntity(errorCode);
	}

	@ExceptionHandler({Exception.class})
	public ResponseEntity<Object> handleAllException(Exception e) {
		log.warn("handleAllException", e);

		ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
		return makeErrorResponseEntity(errorCode);
	}



	// ErrorCode를 받아서 Response를 만드는 메서드
	private ResponseEntity<Object> makeErrorResponseEntity(ErrorCode errorCode) {
		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(ErrorResponse.from(errorCode));
	}

	private ResponseEntity<Object> makeErrorResponseEntity(ErrorCode errorCode, String path) {
		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(ErrorResponse.of(errorCode, path));
	}
}
