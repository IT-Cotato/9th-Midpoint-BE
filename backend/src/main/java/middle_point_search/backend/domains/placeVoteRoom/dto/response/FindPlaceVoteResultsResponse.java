package middle_point_search.backend.domains.placeVoteRoom.dto.response;

import java.util.List;

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
}
