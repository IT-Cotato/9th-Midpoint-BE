package middle_point_search.backend.domains.placeVoteRoom.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PlaceVoteDTO {

	@Getter
	@AllArgsConstructor
	public static class FindPlaceVoteResultsResponse {
		private Long id;
		private String name;
		private String siDo;
		private String siGunGu;
		private String roadNameAddress;
		private Double addressLat;
		private Double addressLong;
		private int count;
		private List<String> voters;
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class VotePlaceRequest {

		@NotNull(message = "choicePlace은 비어 있을 수 없습니다.")
		private Long choicePlace;
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class UpdatePlaceVoteRequest {

		@NotNull(message = "choicePlace은 비어 있을 수 없습니다.")
		private Long choicePlace;
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class FindPlaceVoteCandidatesResponse {
		private Boolean existence;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private List<PlaceCandidate> placeCandidates;

		public static FindPlaceVoteCandidatesResponse from(Boolean existence, List<PlaceCandidate> placeCandidates) {
			return new FindPlaceVoteCandidatesResponse(existence, placeCandidates);
		}

		@Getter
		@AllArgsConstructor(access = AccessLevel.PUBLIC)
		public static class PlaceCandidate {

			private Long id;
			private String name;
			private String siDo;
			private String siGunGu;
			private String roadNameAddress;
			private Double addressLat;
			private Double addressLong;
		}
	}
}
