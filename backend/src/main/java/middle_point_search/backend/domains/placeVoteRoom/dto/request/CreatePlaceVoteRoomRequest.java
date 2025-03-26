package middle_point_search.backend.domains.placeVoteRoom.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import middle_point_search.backend.domains.placeVoteRoom.dto.dto.PlaceCandidateInfo;

public record CreatePlaceVoteRoomRequest(
	@NotEmpty(message = "투표 후보가 제공되지 않았습니다.")
	@Valid
	List<PlaceCandidateInfo> placeCandidates
) {
}