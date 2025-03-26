package middle_point_search.backend.domains.placeVoteRoom.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FindVotedAndVoteItemResponse {
	private Boolean existence;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Long voteItem;

	public static FindVotedAndVoteItemResponse from(Boolean existence, Long voteItem) {
		return new FindVotedAndVoteItemResponse(existence, voteItem);
	}
}
