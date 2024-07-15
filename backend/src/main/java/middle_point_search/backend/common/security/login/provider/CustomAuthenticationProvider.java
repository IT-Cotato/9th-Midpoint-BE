package middle_point_search.backend.common.security.login.provider;

import java.util.Collection;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.security.dto.CustomUserDetails;
import middle_point_search.backend.common.security.dto.CustomUserDetailsImpl;
import middle_point_search.backend.common.security.dto.MemberAuthenticationToken;
import middle_point_search.backend.common.security.login.service.CustomUserDetailsService;
import middle_point_search.backend.domains.member.service.MemberService;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

	private final CustomUserDetailsService userDetailsService;
	private final MemberService memberService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String name = (String)authentication.getPrincipal();
		String password = (String)authentication.getCredentials();
		String roomId = (String)((MemberAuthenticationToken)authentication).getRoomId();

		CustomUserDetails user = (CustomUserDetailsImpl)userDetailsService.loadUserByUsernameAndRoomId(roomId,
			name, password);

		// 비밀번호 확인
		if (!memberService.matchPassword(password, user.getPassword())) {
			throw new BadCredentialsException("비밀번호가 틀렸습니다.");
		}

		Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>)user.getAuthorities();

		return MemberAuthenticationToken.authenticated(roomId, name, password, authorities);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return true;
	}
}
