package middle_point_search.backend.domains.member.domain;

public enum Role {
	USER("유저"),
	GUEST("게스트"),
	;

	private final String name;

	Role(String name) {
		this.name = name;
	}
}
