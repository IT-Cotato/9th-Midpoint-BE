package middle_point_search.backend.domains.placeVoteRoom.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteCandidate;

public record FindPlaceVoteCandidatesResponse(
	Boolean existence,
	@JsonInclude(JsonInclude.Include.NON_NULL)
	List<PlaceCandidateDto> placeCandidateDtos
) {
	public static FindPlaceVoteCandidatesResponse from(Boolean existence, List<PlaceCandidateDto> placeCandidateDtos) {
		return new FindPlaceVoteCandidatesResponse(existence, placeCandidateDtos);
	}

	public record PlaceCandidateDto(
		Long id,
		String name,
		String siDo,
		String siGunGu,
		String roadNameAddress,
		Double addressLat,
		Double addressLong
	) {
		public static PlaceCandidateDto from(PlaceVoteCandidate placeVoteCandidate) {
			return new PlaceCandidateDto(
				placeVoteCandidate.getId(),
				placeVoteCandidate.getName(),
				placeVoteCandidate.getSiDo(),
				placeVoteCandidate.getSiGunGu(),
				placeVoteCandidate.getRoadNameAddress(),
				placeVoteCandidate.getAddressLatitude(),
				placeVoteCandidate.getAddressLongitude());
		}
	}
}