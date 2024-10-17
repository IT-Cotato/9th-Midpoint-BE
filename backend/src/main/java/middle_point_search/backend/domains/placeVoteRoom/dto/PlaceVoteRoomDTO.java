package middle_point_search.backend.domains.placeVoteRoom.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PlaceVoteRoomDTO {
	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class PlaceVoteRoomCreateRequest {

		@NotEmpty(message = "투표 후보가 제공되지 않았습니다.")
		@Valid
		private List<PlaceCandidateInfo> placeCandidates;

		@Getter
		@AllArgsConstructor(access = AccessLevel.PRIVATE)
		public static class PlaceCandidateInfo {

			@NotBlank(message = "name은 비어 있을 수 없습니다.")
			private String name;
			@NotBlank(message = "siDo는 비어 있을 수 없습니다.")
			private String siDo;
			@NotBlank(message = "siGunGu는 비어 있을 수 없습니다.")
			private String siGunGu;
			@NotBlank(message = "roadNameAddress는 비어 있을 수 없습니다.")
			private String roadNameAddress;
			@NotNull
			@Positive(message = "addreesLat은 양수이어야 합니다.")
			private Double addressLat;
			@NotNull
			@Positive(message = "addreesLong은 양수이어야 합니다.")
			private Double addressLong;
		}
	}



	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class PlaceVoteRoomCreateResponse {
		private final Long id;

		public static PlaceVoteRoomCreateResponse from(Long id) {
			return new PlaceVoteRoomCreateResponse(id);
		}
	}


	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class VotedAndVoteItemResponse {
		private Boolean existence;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private Long voteItem;

		public static VotedAndVoteItemResponse from(Boolean existence, Long voteItem) {
			return new VotedAndVoteItemResponse(existence, voteItem);
		}
	}
}

