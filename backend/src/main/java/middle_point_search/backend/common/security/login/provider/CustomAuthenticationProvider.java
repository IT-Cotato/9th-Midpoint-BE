package middle_point_search.backend.common.security.login.provider;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.common.exception.errorCode.UserErrorCode;
import middle_point_search.backend.common.security.dto.CustomUserDetailsImpl;
import middle_point_search.backend.common.security.dto.CustomUsernamePasswordAuthenticationToken;
import middle_point_search.backend.common.security.login.service.CustomUserDetailsService;

@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

	private final CustomUserDetailsService userDetailsService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String username=(String) authentication.getPrincipal();
		String password=(String) authentication.getCredentials();
		String placeRoomId = ((CustomUsernamePasswordAuthenticationToken) authentication).getPlaceRoomId();

		CustomUserDetailsImpl user = (CustomUserDetailsImpl)userDetailsService.loadUserByUsernameAndRoomId(username, placeRoomId);

		// 비밀번호 확인
		if(!matchPassword(password, user.getPassword())) {
			throw new CustomException(UserErrorCode.MEMBER_CREDENTIAL_UNAUTHORIZED);
		}

		return CustomUsernamePasswordAuthenticationToken.of(username,password, user.getRoomId());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return true;
	}

	private boolean matchPassword(String loginPwd, String password) {
		return loginPwd.equals(password);
	}

}
