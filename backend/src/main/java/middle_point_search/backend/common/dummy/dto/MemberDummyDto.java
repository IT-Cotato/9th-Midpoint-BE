package middle_point_search.backend.common.dummy.dto;

import middle_point_search.backend.domains.member.domain.Role;

public record MemberDummyDto(
	String email,
	String password,
	String name,
	Role role
) {
}
