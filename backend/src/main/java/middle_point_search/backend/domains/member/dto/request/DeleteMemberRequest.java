package middle_point_search.backend.domains.member.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DeleteMemberRequest(
	@NotBlank(message = "accessToken은 비어있으면 안됩니다.") String accessToken,
	String withdrawalReason
) {
}

