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
import middle_point_search.backend.domains.placeVoteRoom.dto.PlaceVoteDTO.PlaceVoteCandidatesFindResponse;
import middle_point_search.backend.domains.placeVoteRoom.dto.PlaceVoteDTO.PlaceVoteRequest;
import middle_point_search.backend.domains.placeVoteRoom.dto.PlaceVoteDTO.PlaceVoteResultsFindResponse;
import middle_point_search.backend.domains.placeVoteRoom.dto.PlaceVoteRoomDTO;
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
	public ResponseEntity<DataResponse<List<PlaceVoteResultsFindResponse>>> placeVoteRoomResultGet(
		@PathVariable("roomId") Long roomId
	) {
		List<PlaceVoteResultsFindResponse> response = placeVoteRoomService.findPlaceVoteResults(
			roomId);
		return ResponseEntity.ok(DataResponse.from(response));
	}

	@PostMapping("/rooms/{roomId}")
	@Operation(
		summary = "장소투표하기",
		description = """
			장소투표후보 id를 응답받아 해당하는 id를 가진 투표후보를 투표하도록 한다.
			
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
		@PathVariable("roomId") Long roomId,
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
	public ResponseEntity<DataResponse<Void>> voteUpdate(
		@PathVariable("roomId") Long roomId,
		@RequestBody @Valid PlaceVoteRequest request
	) {
		Member member = memberLoader.getMember();

		placeVoteRoomService.updateVote(member, roomId, request);
		return ResponseEntity.ok(DataResponse.ok());
	}

	@GetMapping("/rooms/{roomId}")
	@Operation(
		summary = "장소투표방 조회하기",
		description = """
			장소투표방 존재여부를 나타내고 존재하면 true, 존재하지않으면 false를 반환한다.
			
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
				responseCode = "422",
				description = "방의 타입이 일치하지 않습니다",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<PlaceVoteCandidatesFindResponse>> placeVoteRoomFind(
		@PathVariable("roomId") Long roomId
	) {
		PlaceVoteCandidatesFindResponse response = placeVoteRoomService.findPlaceVoteCandidates(
			roomId);

		return ResponseEntity.ok(DataResponse.from(response));
	}

	//투표여부
	@GetMapping("/voted/rooms/{roomId}")
	@Operation(
		summary = "장소투표여부 및 투표항목 조회하기",
		description = """
			장소투표여부를 나타내고 투표를 했으면 true, 투표를 하지않았으면 false를 반환한다.
			
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
	public ResponseEntity<DataResponse<PlaceVoteRoomDTO.VotedAndVoteItemResponse>> votedAndVoteItemFind(
		@PathVariable("roomId") Long roomId
	) {
		Member member = memberLoader.getMember();

		PlaceVoteRoomDTO.VotedAndVoteItemResponse votedAndVoteItemResponse = placeVoteRoomService.findVotedAndVoteItem(
			member, roomId);
		return ResponseEntity.ok(DataResponse.from(votedAndVoteItemResponse));
	}
}
