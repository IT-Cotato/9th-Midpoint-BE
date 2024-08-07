package middle_point_search.backend.domains.timeVoteRoom.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.dto.BaseResponse;
import middle_point_search.backend.common.dto.DataResponse;
import middle_point_search.backend.common.dto.ErrorResponse;
import middle_point_search.backend.common.util.MemberLoader;
import middle_point_search.backend.domains.timeVoteRoom.service.TimeVoteRoomService;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.room.domain.Room;

import static middle_point_search.backend.domains.timeVoteRoom.dto.TimeVoteRoomDTO.*;

@Tag(name = "TIME VOTE ROOM API", description = "시간투표에 대한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/time-vote-rooms")
public class TimeVoteRoomController {

	private final TimeVoteRoomService timeVoteRoomService;
	private final MemberLoader memberLoader;

	//투표방 생성
	@PostMapping
	@Operation(
		summary = "시간투표방 생성하기",
		description = """
			날짜(yyyy-mm-dd)를 리스트로 입력을 받아서 시간투표방을 생성한다.
			            
			시간투표방을 생성시 현재 방에 해당하는 사람들은 투표를 할 수 있는 권한이 생긴다.

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
				description = "잘못된 요청입니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "401",
				description = "인증에 실패하였습니다.",
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
		@RequestBody @Valid TimeVoteRoomCreateRequest request) {

		Room room = memberLoader.getRoom();

		TimeVoteRoomCreateResponse response = timeVoteRoomService.createTimeVoteRoom(room, request);

		return ResponseEntity.ok(DataResponse.from(response));
	}

	//투표방 재생성
	@PutMapping
	@Operation(
		summary = "시간투표방 재생성하기",
		description = """
			날짜(yyyy-mm-dd)를 리스트로 입력을 받아서 시간투표방을 재생성한다."
			            
			시간투표방을 재생성시 현재 방에 해당하는 사람들은 재생성된 투표를 할 수 있는 권한이 생긴다.
			            
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
				description = "잘못된 요청입니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "401",
				description = "인증에 실패하였습니다.",
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
		@RequestBody @Valid TimeVoteRoomCreateRequest request) {

		Room room = memberLoader.getRoom();

		TimeVoteRoomCreateResponse response = timeVoteRoomService.recreateTimeVoteRoom(room, request);

		return ResponseEntity.ok(DataResponse.from(response));
	}

	//투표
	@PostMapping("/vote")
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
				responseCode = "401",
				description = "인증에 실패하였습니다.",
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
	public ResponseEntity<?> vote(@RequestBody @Valid TimeVoteRoomVoteRequest request) {

		Member member = memberLoader.getMember();
		Room room = memberLoader.getRoom();

		timeVoteRoomService.vote(member, room, request);

		return ResponseEntity.ok(BaseResponse.ok());
	}

	//재투표,투표 수정
	@PutMapping("/vote")
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
				responseCode = "401",
				description = "인증에 실패하였습니다.",
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
	public ResponseEntity<?> voteUpdate(@RequestBody @Valid TimeVoteRoomVoteRequest request) {

		Member member = memberLoader.getMember();
		Room room = memberLoader.getRoom();

		timeVoteRoomService.updateVote(member, room, request);

		return ResponseEntity.ok(BaseResponse.ok());
	}

	//투표방존재확인
	@GetMapping("/existence")
	@Operation(
		summary = "시간투표방 존재여부 확인하기",
		description = """
			         시간투표방 존재여부를 나타내고 존재하면 true, 존재하지않으면 false를 반환한다.
			         
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
	public ResponseEntity<DataResponse<Boolean>> timeVoteRoomHas() {

		String roomId = memberLoader.getRoomId();

		boolean exists = timeVoteRoomService.hasTimeVoteRoom(roomId);

		return ResponseEntity.ok(DataResponse.from(exists));
	}

	//투표여부
	@GetMapping("/voted")
	@Operation(
		summary = "시간투표여부 확인하기",
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
	public ResponseEntity<DataResponse<Boolean>> votedHas() {

		Member member = memberLoader.getMember();
		Room room = memberLoader.getRoom();

		boolean hasVoted = timeVoteRoomService.hasVoted(member, room);

		return ResponseEntity.ok(DataResponse.from(hasVoted));
	}

	//투표 결과
	@GetMapping("/result")
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
	public ResponseEntity<DataResponse<TimeVoteRoomResultResponse>> timeVoteResultsGet() {

		Room room = memberLoader.getRoom();

		TimeVoteRoomResultResponse result = timeVoteRoomService.getTimeVoteResult(room);

		return ResponseEntity.ok(DataResponse.from(result));
	}

}
