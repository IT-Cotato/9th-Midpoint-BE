package middle_point_search.backend.domains.placeVoteRoom.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

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
	public static class PlaceVoteRoomGetResponse {
		private Boolean existence;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private List<PlaceCandidates> placeCandidates;

		public static PlaceVoteRoomGetResponse from(Boolean existence, List<PlaceCandidates> placeCandidates) {
			return new PlaceVoteRoomGetResponse(existence, placeCandidates);
		}

		@Getter
		@AllArgsConstructor(access = AccessLevel.PUBLIC)
		public static class PlaceCandidates {

			private Long id;
			private String name;
			private String siDo;
			private String siGunGu;
			private String roadNameAddress;
			private Double addressLat;
			private Double addressLong;
		}
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class PlaceVoteStatusResponse {
		private Boolean existence;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private List<PlaceCandidates> allPlaceCandidates;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private List<PlaceCandidates> userVotedCandidates;

		public static PlaceVoteStatusResponse from(Boolean existence, List<PlaceCandidates> allPlaceCandidates, List<PlaceCandidates> userVotedCandidates) {
			return new PlaceVoteStatusResponse(existence, allPlaceCandidates, userVotedCandidates);
		}

		@Getter
		@AllArgsConstructor(access = AccessLevel.PUBLIC)
		public static class PlaceCandidates {
			private Long id;
			private String name;
			private String siDo;
			private String siGunGu;
			private String roadNameAddress;
			private Double addressLat;
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
	@AllArgsConstructor
	public static class PlaceVoteInfoResponse {
		private List<PlaceVoteCandidateInfo> placeCandidates;

		@Getter
		@AllArgsConstructor
		public static class PlaceVoteCandidateInfo {
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
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class PlaceVoteRequest {

		@NotNull(message = "choicePlace은 비어 있을 수 없습니다.")
		private Long choicePlace;
	}
}

