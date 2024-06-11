package middle_point_search.backend.common.security.dto;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

public class MemberAuthenticationToken extends AbstractAuthenticationToken {

	private final String roomId;
	private final String name;
	private String pw;

	public MemberAuthenticationToken(String roomId, String name, String pw) {
		super((Collection)null);
		this.roomId = roomId;
		this.name = name;
		this.pw = pw;
		this.setAuthenticated(false);
	}

	public MemberAuthenticationToken(String roomId, String name, String pw, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.roomId = roomId;
		this.name = name;
		this.pw = pw;
		super.setAuthenticated(true);
	}

	public static MemberAuthenticationToken unauthenticated(String roomId, String name, String pw) {
		return new MemberAuthenticationToken(roomId, name, pw);
	}

	public static MemberAuthenticationToken authenticated(String roomId, String name, String pw,
		Collection<? extends GrantedAuthority> authorities) {
		return new MemberAuthenticationToken(roomId, name, pw, authorities);
	}

	public Object getCredentials() {
		return this.pw;
	}

	public Object getPrincipal() {
		return this.name;
	}

	public Object getRoomId() {
		return this.roomId;
	}

	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		Assert.isTrue(!isAuthenticated,
			"Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
		super.setAuthenticated(false);
	}

	public void eraseCredentials() {
		super.eraseCredentials();
		this.pw = null;
	}
}
