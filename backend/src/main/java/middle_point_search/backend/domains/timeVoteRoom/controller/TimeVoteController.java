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
import middle_point_search.backend.domains.timeVoteRoom.dto.TimeVoteDTO.FindTimeVoteRoomResultResponse;
import middle_point_search.backend.domains.timeVoteRoom.dto.TimeVoteDTO.FindVotedAndVoteItemsResponse;
import middle_point_search.backend.domains.timeVoteRoom.dto.TimeVoteDTO.UpdateTimeVoteRequest;
import middle_point_search.backend.domains.timeVoteRoom.dto.TimeVoteDTO.VoteRequest;
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
				description = """
					인증에 실패하였습니다.[C-101] \n
					Refresh Token이 유효하지 않습니다.[A-003]""",
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
		@PathVariable String roomId,
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
				description = """
					인증에 실패하였습니다.[C-101] \n
					Refresh Token이 유효하지 않습니다.[A-003]""",
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
			),
		}
	)
	public ResponseEntity<DataResponse<Void>> updateTimeVote(
		@PathVariable String roomId,
		@RequestBody @Valid UpdateTimeVoteRequest request
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
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
			),
			@ApiResponse(
				responseCode = "401",
				description = """
					인증에 실패하였습니다.[C-101] \n
					Refresh Token이 유효하지 않습니다.[A-003]""",
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
				description = "생성된 투표방이 없습니다.[V-202]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<FindVotedAndVoteItemsResponse>> findVotedAndVoteItems(
		@PathVariable String roomId
	) {
		Member member = memberLoader.getMember();

		FindVotedAndVoteItemsResponse response = timeVoteRoomService.getVotedAndVoteItems(member, roomId);

		return ResponseEntity.ok(DataResponse.from(response));
	}

	@GetMapping("/result/rooms/{roomId}")
	@Operation(
		summary = "시간투표결과 확인하기",
		description = """
			        시간투표후보날짜들에 대한 결과를 보여준다. 각 날짜에 대한 멤버들의 투표 현황과 총 투표한 인원의 정보를 반환한다.
			
			AccessToken 필요.""",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
			),
			@ApiResponse(
				responseCode = "401",
				description = """
					인증에 실패하였습니다.[C-101] \n
					Refresh Token이 유효하지 않습니다.[A-003]""",
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
				description = "생성된 투표방이 없습니다.[V-202]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<FindTimeVoteRoomResultResponse>> findTimeVoteResult(
		@PathVariable String roomId
	) {
		Long memberId = memberLoader.getMemberId();

		FindTimeVoteRoomResultResponse result = timeVoteRoomService.findTimeVoteResult(memberId, roomId);

		return ResponseEntity.ok(DataResponse.from(result));
	}
}
