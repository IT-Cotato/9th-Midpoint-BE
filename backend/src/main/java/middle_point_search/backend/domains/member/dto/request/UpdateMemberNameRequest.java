package middle_point_search.backend.domains.member.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateMemberNameRequest(
	@Size(min = 2, max = 30) String name
) {
}
