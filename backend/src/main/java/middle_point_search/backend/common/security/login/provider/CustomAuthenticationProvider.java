package middle_point_search.backend.common.security.login.provider;

import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.security.dto.CustomUserDetails;
import middle_point_search.backend.common.security.dto.CustomUserDetailsImpl;
import middle_point_search.backend.common.security.dto.MemberAuthenticationToken;
import middle_point_search.backend.common.security.login.service.CustomUserDetailsService;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

	private final CustomUserDetailsService userDetailsService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String name = (String)authentication.getPrincipal();
		String password = (String)authentication.getCredentials();
		String roomId = (String)((MemberAuthenticationToken)authentication).getRoomId();

		CustomUserDetails user = (CustomUserDetailsImpl)userDetailsService.loadUserByUsernameAndRoomId(roomId,
			name, password);

		// 비밀번호 확인
		if (!matchPassword(password, user.getPassword())) {
			throw new BadCredentialsException("비밀번호가 틀렸습니다.");
		}

		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("USER"));

		return MemberAuthenticationToken.authenticated(roomId, name, password, authorities);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return true;
	}

	private boolean matchPassword(String loginPwd, String password) {
		return loginPwd.equals(password);
	}

}
