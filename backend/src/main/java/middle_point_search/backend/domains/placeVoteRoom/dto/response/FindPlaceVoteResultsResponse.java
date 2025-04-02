package middle_point_search.backend.domains.placeVoteRoom.dto.response;

import java.util.List;

import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVote;
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteCandidate;

public record FindPlaceVoteResultsResponse(
	long id,
	String name,
	String siDo,
	String siGunGu,
	String roadNameAddress,
	Double addressLat,
	Double addressLong,
	int count,
	List<String> voters
) {
	public static FindPlaceVoteResultsResponse of(PlaceVoteCandidate placeVoteCandidate, List<PlaceVote> votes) {
		return new FindPlaceVoteResultsResponse(
			placeVoteCandidate.getId(),
			placeVoteCandidate.getName(),
			placeVoteCandidate.getSiDo(),
			placeVoteCandidate.getSiGunGu(),
			placeVoteCandidate.getRoadNameAddress(),
			placeVoteCandidate.getAddressLatitude(),
			placeVoteCandidate.getAddressLongitude(),
			votes.size(),
			votes.stream().map(voter -> voter.getMember().getName()).toList()
		);
	}
}
