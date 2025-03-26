package middle_point_search.backend.domains.placeVoteRoom.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

public record FindPlaceVoteCandidatesResponse(
	Boolean existence,
	@JsonInclude(JsonInclude.Include.NON_NULL)
	List<PlaceCandidate> placeCandidates
) {
	public static FindPlaceVoteCandidatesResponse from(Boolean existence, List<PlaceCandidate> placeCandidates) {
		return new FindPlaceVoteCandidatesResponse(existence, placeCandidates);
	}

	public record PlaceCandidate(
		Long id,
		String name,
		String siDo,
		String siGunGu,
		String roadNameAddress,
		Double addressLat,
		Double addressLong
	) {}
}