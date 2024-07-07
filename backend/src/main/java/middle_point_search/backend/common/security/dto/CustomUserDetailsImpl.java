package middle_point_search.backend.common.security.dto;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import middle_point_search.backend.domains.member.domain.Member;


@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomUserDetailsImpl implements CustomUserDetails {

	private String roomId;
	private String name;
	private String pw;
	private String authority;
	private boolean enabled;
	public static CustomUserDetailsImpl of(String roomId, String name, String pw, String authority, boolean enabled) {
		return new CustomUserDetailsImpl(roomId, name, pw, authority, enabled);
	}

	public static CustomUserDetailsImpl from(Member member) {
		String roomId = member.getRoom().getIdentityNumber();
		String name = member.getName();
		String pw = member.getPw();
		String authority = "USER";
		boolean enabled = true;

		return new CustomUserDetailsImpl(roomId, name, pw, authority, enabled);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		ArrayList<GrantedAuthority> auth = new ArrayList<>();
		auth.add(new SimpleGrantedAuthority(authority));
		return auth;
	}

	@Override
	public String getPassword() {
		return pw;
	}

	@Override
	public String getUsername() {
		return name;
	}

	@Override
	public String getRoomId() {
		return roomId;
	}


	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
}
