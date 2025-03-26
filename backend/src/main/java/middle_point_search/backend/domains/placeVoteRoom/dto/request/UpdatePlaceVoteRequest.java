package middle_point_search.backend.domains.placeVoteRoom.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdatePlaceVoteRequest(
	@NotNull(message = "choicePlace은 비어 있을 수 없습니다.") Long choicePlace
) {}