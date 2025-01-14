package middle_point_search.backend.domains.placeVoteRoom.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.dto.DataResponse;
import middle_point_search.backend.common.dto.ErrorResponse;
import middle_point_search.backend.common.util.MemberLoader;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.placeVoteRoom.dto.PlaceVoteDTO.PlaceVoteRequest;
import middle_point_search.backend.domains.placeVoteRoom.dto.PlaceVoteDTO.PlaceVoteResultsFindResponse;
import middle_point_search.backend.domains.placeVoteRoom.dto.PlaceVoteRoomDTO.VotedAndVoteItemResponse;
import middle_point_search.backend.domains.placeVoteRoom.service.PlaceVoteService;

@Tag(name = "PLACE VOTE API", description = "장소 투표에 대한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/place-votes")
public class PlaceVoteController {

	private final MemberLoader memberLoader;
	private final PlaceVoteService placeVoteRoomService;

	@GetMapping("/result/rooms/{roomId}")
	@Operation(
		summary = "장소투표방 결과 조회하기",
		description = """
			각 장소후보별로 해당하는 장소투표후보 id, 장소투표후보이름, 투표수, 투표한 멤버 id(이름)리스트를 반환한다.
			
			AccessToken 필요.""",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
			),
			@ApiResponse(
				responseCode = "400",
				description = "요청 파라미터가 잘못되었습니다.[C-202]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "401",
				description = "인증에 실패하였습니다.[C-101]"
			),
			@ApiResponse(
				responseCode = "402",
				description = "Access Token을 재발급해야합니다.[A-004]"
			),
			@ApiResponse(
				responseCode = "403",
				description = "해당 방의 회원이 아닙니다.[MR-003]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "404",
				description = "생성된 투표방이 없습니다.[V-202]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<List<PlaceVoteResultsFindResponse>>> placeVoteRoomResultGet(
		@PathVariable("roomId") String roomId
	) {
		Long memberId = memberLoader.getMemberId();

		List<PlaceVoteResultsFindResponse> response = placeVoteRoomService.findPlaceVoteResults(memberId, roomId);

		return ResponseEntity.ok(DataResponse.from(response));
	}

	@PostMapping("/rooms/{roomId}")
	@Operation(
		summary = "장소투표하기",
		description = """
			장소투표후보 id를 응답받아 해당하는 id를 가진 투표후보를 투표하도록 한다.
			
			AccessToken 필요.""",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
			),
			@ApiResponse(
				responseCode = "400",
				description = "요청 파라미터가 잘못되었습니다.[C-202]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "401",
				description = "인증에 실패하였습니다.[C-101]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "402",
				description = "Access Token을 재발급해야합니다.[A-004]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "403",
				description = "해당 방의 회원이 아닙니다.[MR-003]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "404",
				description = "생성된 투표방이 없습니다.[V-202] or 투표 후보가 아닙니다.[V-101]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "409",
				description = "이미 투표를 하였습니다.[V-301]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<Void>> vote(
		@PathVariable("roomId") String roomId,
		@RequestBody @Valid PlaceVoteRequest request
	) {
		Member member = memberLoader.getMember();

		placeVoteRoomService.vote(member, roomId, request);
		return ResponseEntity.ok(DataResponse.ok());
	}

	@PutMapping("/rooms/{roomId}")
	@Operation(
		summary = "장소 재투표하기",
		description = """
			장소투표후보 id를 응답받아 해당하는 id를 가진 투표후보를 재투표하도록 한다.
			
			AccessToken 필요.""",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
			),
			@ApiResponse(
				responseCode = "400",
				description = "요청 파라미터가 잘못되었습니다.[C-202]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "401",
				description = "인증에 실패하였습니다.[C-101]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "402",
				description = "Access Token을 재발급해야합니다.[A-004]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "403",
				description = "해당 방의 회원이 아닙니다.[MR-003]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "404",
				description = "생성된 투표방이 없습니다.[V-202] or 투표 후보가 아닙니다.[V-101] or 투표를 한 적이 없습니다.[V-201]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<Void>> voteUpdate(
		@PathVariable("roomId") String roomId,
		@RequestBody @Valid PlaceVoteRequest request
	) {
		Member member = memberLoader.getMember();

		placeVoteRoomService.updateVote(member, roomId, request);
		return ResponseEntity.ok(DataResponse.ok());
	}

	//투표여부
	@GetMapping("/voted/rooms/{roomId}")
	@Operation(
		summary = "장소투표여부 및 투표항목 조회하기",
		description = """
			장소투표여부를 나타내고 투표를 했으면 true, 투표를 하지않았으면 false를 반환한다.
			
			AccessToken 필요.""",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
			),
			@ApiResponse(
				responseCode = "401",
				description = "인증에 실패하였습니다.[C-101]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "402",
				description = "Access Token을 재발급해야합니다.[A-004]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "403",
				description = "해당 방의 회원이 아닙니다.[MR-003]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<VotedAndVoteItemResponse>> votedAndVoteItemFind(
		@PathVariable("roomId") String roomId
	) {
		Member member = memberLoader.getMember();

		VotedAndVoteItemResponse votedAndVoteItemResponse = placeVoteRoomService.findVotedAndVoteItem(
			member, roomId);
		return ResponseEntity.ok(DataResponse.from(votedAndVoteItemResponse));
	}
}
