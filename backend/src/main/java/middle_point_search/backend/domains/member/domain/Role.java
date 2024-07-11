package middle_point_search.backend.domains.member.domain;

public enum Role {
	USER("ROLE_USER"),
	GUEST("ROLE_GUEST"),
	;

	private final String name;

	Role(String name) {
		this.name = name;
	}
}
