package middle_point_search.backend.domains.member.domain;

import lombok.Getter;

@Getter
public enum Role {
	USER("USER"),
	GUEST("GUEST"),
	ADMIN("ADMIN"),
	;

	private final String value;

	Role(String value) {
		this.value = value;
	}
}
