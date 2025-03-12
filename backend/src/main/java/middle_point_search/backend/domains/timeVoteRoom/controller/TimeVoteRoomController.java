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
import middle_point_search.backend.domains.timeVoteRoom.dto.request.CreateTimeVoteRoomRequest;
import middle_point_search.backend.domains.timeVoteRoom.dto.request.UpdateTimeVoteRoomRequest;
import middle_point_search.backend.domains.timeVoteRoom.dto.response.CreateTimeVoteRoomResponse;
import middle_point_search.backend.domains.timeVoteRoom.dto.response.FindTimeVoteRoomResponse;
import middle_point_search.backend.domains.timeVoteRoom.service.TimeVoteRoomService;

@Tag(name = "TIME VOTE ROOM API", description = "시간투표방에 대한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/time-vote-rooms")
public class TimeVoteRoomController {

	private final TimeVoteRoomService timeVoteRoomService;
	private final MemberLoader memberLoader;

	@PostMapping("/rooms/{roomId}")
	@Operation(
		summary = "시간투표방 생성하기",
		description = """
			날짜(yyyy-mm-dd)를 리스트로 입력을 받아서 시간투표방을 생성한다.
			
			시간투표방을 생성시 현재 방에 해당하는 사람들은 투표를 할 수 있는 권한이 생긴다.
			
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
				responseCode = "409",
				description = "이미 투표방이 존재합니다.[V-302]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<CreateTimeVoteRoomResponse>> createTimeVoteRoom(
		@PathVariable("roomId") String roomId,
		@RequestBody @Valid CreateTimeVoteRoomRequest request
	) {
		Long memberId = memberLoader.getMemberId();

		CreateTimeVoteRoomResponse response = timeVoteRoomService.createTimeVoteRoom(memberId, roomId, request);

		return ResponseEntity.ok(DataResponse.from(response));
	}

	@PutMapping("/rooms/{roomId}")
	@Operation(
		summary = "시간투표방 업데이트하기",
		description = """
			날짜(yyyy-mm-dd)를 리스트로 입력을 받아서 시간투표방을 업데이트한다.
			
			기존 투표 내역은 사라진다.
			
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
				description = "시간투표방이 존재하지 않습니다.[TV-001]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<Void>> updateTimeVoteRoom(
		@PathVariable("roomId") String roomId,
		@RequestBody @Valid UpdateTimeVoteRoomRequest request
	) {
		Long memberId = memberLoader.getMemberId();

		timeVoteRoomService.updateTimeVoteRoom(memberId, roomId, request);

		return ResponseEntity.ok(DataResponse.ok());
	}

	@GetMapping("/rooms/{roomId}")
	@Operation(
		summary = "시간투표방 조회하기",
		description = """
			시간투표방 존재여부를 나타내고 존재하면 true, 존재하지않으면 false를 반환한다.
			
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
			)
		}
	)
	public ResponseEntity<DataResponse<FindTimeVoteRoomResponse>> FindTimeVoteRoom(
		@PathVariable("roomId") String roomId
	) {
		Long memberId = memberLoader.getMemberId();

		FindTimeVoteRoomResponse response = timeVoteRoomService.findTimeVoteRoomAndMakeDTO(memberId, roomId);

		return ResponseEntity.ok(DataResponse.from(response));
	}
}
