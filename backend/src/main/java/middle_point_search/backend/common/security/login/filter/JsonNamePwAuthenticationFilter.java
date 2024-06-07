package middle_point_search.backend.common.security.login.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import middle_point_search.backend.common.security.dto.CustomUsernamePasswordAuthenticationToken;

public class JsonNamePwAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private static final String DEFAULT_LOGIN_REQUEST_URL = "/api/auth/login";
	private static final String HTTP_METHOD = "POST";    //HTTP 메서드의 방식은 POST 이다.
	private static final String CONTENT_TYPE = "application/json";//json 타입의 데이터로만 로그인을 진행한다.
	private final ObjectMapper objectMapper;
	private static final String USERNAME_KEY = "name";
	private static final String PASSWORD_KEY = "pw";
	private static final String PLACE_ROOM_ID_KEY = "placeRoomId";

	private static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCHER =
		new AntPathRequestMatcher(DEFAULT_LOGIN_REQUEST_URL, HTTP_METHOD); //=>   /login 의 요청에, POST로 온 요청에 매칭

	public JsonNamePwAuthenticationFilter(ObjectMapper objectMapper) {
		super(DEFAULT_LOGIN_PATH_REQUEST_MATCHER);   // 위에서 설정한  /oauth2/login/* 의 요청에, GET으로 온 요청을 처리하기 위해 설정
		this.objectMapper = objectMapper;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException, IOException {
		// 요청 형식 확인
		if (request.getContentType() == null || !request.getContentType().equals(CONTENT_TYPE)) {
			throw new AuthenticationServiceException("Authentication Content-Type not supported: " + request.getContentType());
		}

		String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

		Map<String, String> usernamePasswordMap = objectMapper.readValue(messageBody, Map.class);

		String name = usernamePasswordMap.get(USERNAME_KEY);
		String pw = usernamePasswordMap.get(PASSWORD_KEY);
		String placeRoomId = usernamePasswordMap.get(PLACE_ROOM_ID_KEY);

		CustomUsernamePasswordAuthenticationToken authRequest = CustomUsernamePasswordAuthenticationToken.of(name, pw, placeRoomId);

		return this.getAuthenticationManager().authenticate(authRequest);
	}
}