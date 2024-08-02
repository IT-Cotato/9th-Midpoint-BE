package middle_point_search.backend.common.filter;

import static middle_point_search.backend.common.exception.errorCode.CommonErrorCode.*;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import middle_point_search.backend.common.dto.ErrorResponse;
import middle_point_search.backend.common.util.ResponseWriter;

public class RateLimitFilter extends OncePerRequestFilter {

	private static final String DEFAULT_ROOM_CREATE_REQUEST_URL = "/api/rooms";
	private static final String HTTP_METHOD = "POST";
	private static final int REQUEST_LIMIT_PER_TIME = 20;
	private static final int TIME = 1; //분

	private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		String requestURI = request.getRequestURI();
		String httpMethod = request.getMethod();

		if (shouldApplyRateLimiting(requestURI, httpMethod)) {
			String clientIp = getClientIP(request);

			Bucket bucket = buckets.computeIfAbsent(clientIp, this::newBucket);

			if (bucket.tryConsume(1)) {
				filterChain.doFilter(request, response);
			} else {
				ErrorResponse errorResponse = ErrorResponse.from(TOO_MANY_REQUESTS);

				ResponseWriter.writeResponse(response, errorResponse, HttpStatus.TOO_MANY_REQUESTS);
			}
		} else {
			filterChain.doFilter(request, response);
		}

	}

	//url 및 httpMethod 확인하여 일치하면 true
	private boolean shouldApplyRateLimiting(String requestURI, String httpMethod) {
		return requestURI.startsWith(DEFAULT_ROOM_CREATE_REQUEST_URL) && HTTP_METHOD.equalsIgnoreCase(httpMethod);
	}

	//Bucket 생성
	private Bucket newBucket(String clientIp) {
		Bandwidth limit = Bandwidth.classic(REQUEST_LIMIT_PER_TIME, Refill.greedy(REQUEST_LIMIT_PER_TIME, Duration.ofMinutes(TIME)));
		return Bucket4j.builder().addLimit(limit).build();
	}

	//사용자의 IP 가져오기
	private String getClientIP(HttpServletRequest request) {
		String xfHeader = request.getHeader("X-Forwarded-For");
		if (xfHeader == null) {
			return request.getRemoteAddr();
		}
		return xfHeader.split(",")[0];
	}
}
