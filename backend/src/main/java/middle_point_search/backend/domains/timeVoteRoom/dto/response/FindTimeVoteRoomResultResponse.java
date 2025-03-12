package middle_point_search.backend.domains.timeVoteRoom.dto.response;

import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import middle_point_search.backend.domains.timeVoteRoom.dto.dto.TimeVoteDetail;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FindTimeVoteRoomResultResponse {
	private Map<String, List<TimeVoteDetail>> result;
	private int totalMemberNum;

	public static FindTimeVoteRoomResultResponse from(Map<String, List<TimeVoteDetail>> result, int totalMemberNum) {
		return new FindTimeVoteRoomResultResponse(result, totalMemberNum);
	}
}