package middle_point_search.backend.domains.timeVoteRoom.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
import middle_point_search.backend.domains.timeVoteRoom.dto.TimeVoteDTO.TimeVoteRoomResultResponse;
import middle_point_search.backend.domains.timeVoteRoom.dto.TimeVoteDTO.VoteRequest;
import middle_point_search.backend.domains.timeVoteRoom.dto.TimeVoteDTO.VotedAndVoteItemsGetResponse;
import middle_point_search.backend.domains.timeVoteRoom.service.TimeVoteService;

@Tag(name = "TIME VOTE API", description = "시간투표에 대한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/time-votes")
public class TimeVoteController {

	private final MemberLoader memberLoader;
	private final TimeVoteService timeVoteRoomService;

	@PostMapping("/rooms/{roomId}")
	@Operation(
		summary = "시간투표하기",
		description = """
			가능한 투표후보날짜의 가능한 시작일시(yyyy-mm-dd hh:mm), 가능한 마지막일시(yyyy-mm-dd hh:mm)를 입력을 받아서 투표한다.
			
			AccessToken 필요.""",
		parameters = {
			@Parameter(name = "RoomId", description = "roomId 필요", in = ParameterIn.HEADER),
			@Parameter(name = "RoomType", description = "roomType 필요. [TOGETHER, SELF] 중 하나", in = ParameterIn.HEADER)
		},
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
			),
			@ApiResponse(
				responseCode = "400",
				description = "투표 후보가 아닙니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "401",
				description = "인증에 실패하였습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "402",
				description = "인증 토큰이 유효하지 않습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "403",
				description = "접근이 거부되었습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "404",
				description = "생성된 투표방이 없습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "409",
				description = "이미 투표를 하였습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "422",
				description = "방의 타입이 일치하지 않습니다",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<Void>> vote(
		@PathVariable Long roomId,
		@RequestBody @Valid VoteRequest request
	) {
		Member member = memberLoader.getMember();

		timeVoteRoomService.vote(member, roomId, request);

		return ResponseEntity.ok(DataResponse.ok());
	}

	@PutMapping("/rooms/{roomId}")
	@Operation(
		summary = "시간 재투표하기",
		description = """
			가능한 투표후보날짜의 가능한 시작일시(yyyy-mm-dd hh:mm), 가능한 마지막일시(yyyy-mm-dd hh:mm)를 입력을 받아서 재투표한다.
			
			AccessToken 필요.""",
		parameters = {
			@Parameter(name = "RoomId", description = "roomId 필요", in = ParameterIn.HEADER),
			@Parameter(name = "RoomType", description = "roomType 필요. [TOGETHER, SELF] 중 하나", in = ParameterIn.HEADER)
		},
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
			),
			@ApiResponse(
				responseCode = "400",
				description = "투표 후보가 아닙니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "401",
				description = "인증에 실패하였습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "402",
				description = "인증 토큰이 유효하지 않습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "403",
				description = "접근이 거부되었습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "404",
				description = "생성된 투표방이 없습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "404",
				description = "투표를 한 적이 없습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "422",
				description = "방의 타입이 일치하지 않습니다",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<?> voteUpdate(
		@PathVariable Long roomId,
		@RequestBody @Valid VoteRequest request
	) {
		Member member = memberLoader.getMember();

		timeVoteRoomService.updateVote(member, roomId, request);

		return ResponseEntity.ok(DataResponse.ok());
	}

	@GetMapping("/voted/rooms/{roomId}")
	@Operation(
		summary = "시간투표여부 및 투표항목들 조회",
		description = """
			         시간투표여부를 나타내고 투표를 했으면 true, 투표를 하지않았으면 false를 반환한다.
			
			AccessToken 필요.""",
		parameters = {
			@Parameter(name = "RoomId", description = "roomId 필요", in = ParameterIn.HEADER),
			@Parameter(name = "RoomType", description = "roomType 필요. [TOGETHER, SELF] 중 하나", in = ParameterIn.HEADER)
		},
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
			),
			@ApiResponse(
				responseCode = "401",
				description = "인증에 실패하였습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "402",
				description = "인증 토큰이 유효하지 않습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "403",
				description = "접근이 거부되었습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "404",
				description = "생성된 투표방이 없습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "422",
				description = "방의 타입이 일치하지 않습니다",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<VotedAndVoteItemsGetResponse>> votedAndVoteItemsGet(
		@PathVariable Long roomId
	) {
		Member member = memberLoader.getMember();

		VotedAndVoteItemsGetResponse response = timeVoteRoomService.getVotedAndVoteItems(member, roomId);

		return ResponseEntity.ok(DataResponse.from(response));
	}

	@GetMapping("/result/rooms/{roomId}")
	@Operation(
		summary = "시간투표결과 확인하기",
		description = """
			        시간투표후보날짜들에 대한 결과를 보여준다. 각 날짜에 대한 멤버들의 투표 현황과 총 투표한 인원의 정보를 반환한다.
			
			AccessToken 필요.""",
		parameters = {
			@Parameter(name = "RoomId", description = "roomId 필요", in = ParameterIn.HEADER),
			@Parameter(name = "RoomType", description = "roomType 필요. [TOGETHER, SELF] 중 하나", in = ParameterIn.HEADER)
		},
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
			),
			@ApiResponse(
				responseCode = "401",
				description = "인증에 실패하였습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "402",
				description = "인증 토큰이 유효하지 않습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "403",
				description = "접근이 거부되었습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "404",
				description = "생성된 투표방이 없습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "422",
				description = "방의 타입이 일치하지 않습니다",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}

	)
	public ResponseEntity<DataResponse<TimeVoteRoomResultResponse>> timeVoteResultsGet(
		@PathVariable Long roomId
	) {
		TimeVoteRoomResultResponse result = timeVoteRoomService.findTimeVoteResult(roomId);

		return ResponseEntity.ok(DataResponse.from(result));
	}
}
