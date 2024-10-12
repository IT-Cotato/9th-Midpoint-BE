package middle_point_search.backend.common.security.filter.loginFilter;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.repository.MemberRepository;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String memberName) throws UsernameNotFoundException {
		Member member = memberRepository.findByName(memberName)
			.orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));

		String pw = member.getPw();
		String role = member.getRole().getValue();

		return User.builder()
			.username(memberName)
			.password(pw)
			.roles(role)
			.build();
	}
}
