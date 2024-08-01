package middle_point_search.backend.domains.placeVoteRoom.controller;


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
import middle_point_search.backend.domains.placeVoteRoom.service.PlaceVoteRoomService;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.room.domain.Room;
import static middle_point_search.backend.domains.placeVoteRoom.dto.PlaceVoteRoomDTO.*;



@Tag(name = "PLACE VOTE ROOM API", description = "장소 투표에 대한 API입니다.")

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/place-vote-rooms")
public class PlaceVoteRoomController {

	private final PlaceVoteRoomService placeVoteRoomService;
	private final MemberLoader memberLoader;

	//투표방 생성
	@PostMapping
	@Operation(
		summary = "장소투표방 생성하기",
		description = """
			장소후보를 리스트로 입력을 받아서 장소투표방을 생성한다.
						
			장소투표방을 생성시 현재 방에 해당하는 사람들은 투표를 할 수 있는 권한이 생긴다.
						
			AccessToken 필요.""",
		parameters = {
			@Parameter(name = "RoomId", description = "roomId 필요", required = true, in = ParameterIn.HEADER)
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
				responseCode = "409",
				description = "이미 투표방이 존재합니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<PlaceVoteRoomCreateResponse>> placeVoteRoomCreate(
		@RequestBody @Valid PlaceVoteRoomCreateRequest request) {

		Room room = memberLoader.getRoom();

		PlaceVoteRoomCreateResponse response = placeVoteRoomService.createPlaceVoteRoom(room, request);
		return ResponseEntity.ok(DataResponse.from(response));
	}

	@PutMapping
	@Operation(
		summary = "장소투표방 재생성하기",
		description = """
			장소후보를 리스트로 입력을 받아서 장소투표방을 재생성한다.
						
			장소투표방을 재생성시 현재 방에 해당하는 사람들은 재생성된 투표를 할 수 있는 권한이 생긴다.
						
			AccessToken 필요.""",
		parameters = {
			@Parameter(name = "RoomId", description = "roomId 필요", required = true, in = ParameterIn.HEADER)
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
				responseCode = "404",
				description = "생성된 투표방이 없습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<PlaceVoteRoomCreateResponse>> placeVoteRoomRecreate(
		@RequestBody @Valid PlaceVoteRoomCreateRequest request) {

		Room room = memberLoader.getRoom();

		PlaceVoteRoomCreateResponse response = placeVoteRoomService.recreatePlaceVoteRoom(room, request);
		return ResponseEntity.ok(DataResponse.from(response));
	}

	//투표 결과
	@GetMapping("/result")
	@Operation(
		summary = "장소투표방 결과 조회하기",
		description = """
			각 장소후보별로 해당하는 장소투표후보 id, 장소투표후보이름, 투표수, 투표한 멤버 id(이름)리스트를 반환한다.
						
			AccessToken 필요.""",
		parameters = {
			@Parameter(name = "RoomId", description = "roomId 필요", required = true, in = ParameterIn.HEADER)
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
				responseCode = "404",
				description = "생성된 투표방이 없습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<PlaceVoteInfoResponse>> placeVoteRoomGet() {

		Room room = memberLoader.getRoom();

		PlaceVoteInfoResponse response = placeVoteRoomService.getPlaceVoteRoom(room);
		return ResponseEntity.ok(DataResponse.from(response));
	}

	//투표
	@PostMapping("/vote")
	@Operation(
		summary = "장소투표하기",
		description = """
			장소투표후보 id를 응답받아 해당하는 id를 가진 투표후보를 투표하도록 한다.
						
			AccessToken 필요.""",
		parameters = {
			@Parameter(name = "RoomId", description = "roomId 필요", required = true, in = ParameterIn.HEADER)
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
				responseCode = "404",
				description = "생성된 투표방이 없습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "404",
				description = "투표 후보가 아닙니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "409",
				description = "이미 투표를 하였습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<?> vote(@RequestBody @Valid PlaceVoteRequest request) {

		Member member = memberLoader.getMember();
		Room room = memberLoader.getRoom();

		placeVoteRoomService.vote(member, room, request);
		return ResponseEntity.ok(BaseResponse.ok());
	}

	//재투표
	@PutMapping("/vote")
	@Operation(
		summary = "장소 재투표하기",
		description = """
			장소투표후보 id를 응답받아 해당하는 id를 가진 투표후보를 재투표하도록 한다.
						
			AccessToken 필요.""",
		parameters = {
			@Parameter(name = "RoomId", description = "roomId 필요", required = true, in = ParameterIn.HEADER)
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
				responseCode = "404",
				description = "생성된 투표방이 없습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "404",
				description = "투표 후보가 아닙니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "404",
				description = "투표를 한 적이 없습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<?> voteUpdate(@RequestBody @Valid PlaceVoteRequest request) {

		Member member = memberLoader.getMember();
		Room room = memberLoader.getRoom();

		placeVoteRoomService.updateVote(member, room, request);
		return ResponseEntity.ok(BaseResponse.ok());
	}

	//투표방 존재 확인
	@GetMapping("/existence")
	@Operation(
		summary = "장소투표방 존재여부 확인하기",
		description = """
			장소투표방 존재여부를 나타내고 존재하면 true, 존재하지않으면 false를 반환한다.
						
			AccessToken 필요.""",
		parameters = {
			@Parameter(name = "RoomId", description = "roomId 필요", required = true, in = ParameterIn.HEADER)
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
			)
		}
	)
	public ResponseEntity<DataResponse<Boolean>> placeVoteRoomHas() {

		String roomId = memberLoader.getRoomId();

		boolean exists = placeVoteRoomService.hasPlaceVoteRoom(roomId);
		return ResponseEntity.ok(DataResponse.from(exists));
	}

	//투표여부
	@GetMapping("/voted")
	@Operation(
		summary = "장소투표여부 확인하기",
		description = """
			장소투표여부를 나타내고 투표를 했으면 true, 투표를 하지않았으면 false를 반환한다.
						
			AccessToken 필요.""",
		parameters = {
			@Parameter(name = "RoomId", description = "roomId 필요", required = true, in = ParameterIn.HEADER)
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
				responseCode = "404",
				description = "생성된 투표방이 없습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<Boolean>> votedHas() {

		Member member = memberLoader.getMember();
		Room room = memberLoader.getRoom();

		boolean hasVoted = placeVoteRoomService.hasVoted(member, room);
		return ResponseEntity.ok(DataResponse.from(hasVoted));
	}
}
