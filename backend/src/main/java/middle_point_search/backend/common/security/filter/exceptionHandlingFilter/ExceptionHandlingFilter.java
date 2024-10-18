package middle_point_search.backend.common.security.filter.exceptionHandlingFilter;

import java.util.List;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.dto.ErrorResponse;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.common.exception.errorCode.CommonErrorCode;
import middle_point_search.backend.common.exception.errorCode.ErrorCode;
import middle_point_search.backend.common.util.ResponseWriter;

@Slf4j
public class ExceptionHandlingFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) {
		try {
			filterChain.doFilter(request, response);
		} catch (CustomException e) {
			log.warn("ExceptionHandlingFilter: {}", e.getMessage());

			ErrorResponse errorResponse = ErrorResponse.from(e);
			ResponseWriter.writeResponse(response, errorResponse, e.getHttpStatus());
		} catch (Exception e) {
			log.warn("ExceptionHandlingFilter: {}", e.getMessage());

			ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;

			ErrorResponse errorResponse = ErrorResponse.of(errorCode, List.of(e.getMessage()));
			ResponseWriter.writeResponse(response, errorResponse, errorCode.getHttpStatus());
		}
	}
}

