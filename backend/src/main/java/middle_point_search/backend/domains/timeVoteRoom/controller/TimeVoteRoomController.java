package middle_point_search.backend.domains.timeVoteRoom.controller;

import static middle_point_search.backend.domains.timeVoteRoom.dto.TimeVoteRoomDTO.*;

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
import middle_point_search.backend.domains.timeVoteRoom.service.TimeVoteRoomService;

@Tag(name = "TIME VOTE ROOM API", description = "시간투표방에 대한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/time-vote-rooms")
public class TimeVoteRoomController {

	private final TimeVoteRoomService timeVoteRoomService;

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
				description = "잘못된 요청입니다.",
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
				responseCode = "409",
				description = "이미 투표방이 존재합니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<TimeVoteRoomCreateResponse>> timeVoteRoomCreate(
		@PathVariable("roomId") Long roomId,
		@RequestBody @Valid TimeVoteRoomCreateRequest request
	) {
		TimeVoteRoomCreateResponse response = timeVoteRoomService.createTimeVoteRoom(roomId, request);

		return ResponseEntity.ok(DataResponse.from(response));
	}

	@PutMapping("/rooms/{roomId}")
	@Operation(
		summary = "시간투표방 재생성하기",
		description = """
			날짜(yyyy-mm-dd)를 리스트로 입력을 받아서 시간투표방을 재생성한다."
			
			시간투표방을 재생성시 현재 방에 해당하는 사람들은 재생성된 투표를 할 수 있는 권한이 생긴다.
			
			AccessToken 필요.""",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
			),
			@ApiResponse(
				responseCode = "400",
				description = "잘못된 요청입니다.",
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
				responseCode = "422",
				description = "방의 타입이 일치하지 않습니다",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<TimeVoteRoomCreateResponse>> timeVoteRoomRecreate(
		@PathVariable("roomId") Long roomId,
		@RequestBody @Valid TimeVoteRoomCreateRequest request
	) {
		TimeVoteRoomCreateResponse response = timeVoteRoomService.recreateTimeVoteRoom(roomId, request);

		return ResponseEntity.ok(DataResponse.from(response));
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
				responseCode = "422",
				description = "방의 타입이 일치하지 않습니다",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<TimeVoteRoomGetResponse>> timeVoteRoomGet(
		@PathVariable("roomId") Long roomId
	) {
		TimeVoteRoomGetResponse response = timeVoteRoomService.findTimeVoteRoomAndMakeDTO(roomId);

		return ResponseEntity.ok(DataResponse.from(response));
	}
}
