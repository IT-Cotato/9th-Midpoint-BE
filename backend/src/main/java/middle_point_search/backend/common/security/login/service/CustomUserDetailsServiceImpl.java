package middle_point_search.backend.common.security.login.service;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.common.security.dto.CustomUserDetailsImpl;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.repository.MemberRepository;

@RequiredArgsConstructor
@Component
@Slf4j
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsernameAndRoomId(String placeRoomId, String username) throws UsernameNotFoundException {
		Member member = memberRepository
			.findByRoom_IdentityNumberAndName(placeRoomId, username)
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));


		return CustomUserDetailsImpl.of(placeRoomId, username, member.getPw(), "USER", true);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return null;
	}
}
