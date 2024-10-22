package middle_point_search.backend.domains.placeVoteRoom.controller;

import static middle_point_search.backend.domains.placeVoteRoom.dto.PlaceVoteRoomDTO.*;

import org.springframework.http.ResponseEntity;
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
import middle_point_search.backend.domains.placeVoteRoom.service.PlaceVoteRoomService;

@Tag(name = "PLACE VOTE ROOM API", description = "장소 투표 방에 대한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/place-vote-rooms")
public class PlaceVoteRoomController {

	private final PlaceVoteRoomService placeVoteRoomService;
	private final MemberLoader memberLoader;

	@PostMapping("/rooms/{roomId}")
	@Operation(
		summary = "장소투표방 생성하기",
		description = """
			장소후보를 리스트로 입력을 받아서 장소투표방을 생성한다.
			
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
				description = "존재하지 않는 방입니다.[R-201]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "409",
				description = "이미 투표방이 존재합니다.[V-302]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<PlaceVoteRoomCreateResponse>> placeVoteRoomCreate(
		@PathVariable("roomId") Long roomId,
		@RequestBody @Valid PlaceVoteRoomCreateRequest request
	) {
		Member member = memberLoader.getMember();
		PlaceVoteRoomCreateResponse response = placeVoteRoomService.createPlaceVoteRoom(
			member.getId(),
			roomId,
			request);

		return ResponseEntity.ok(DataResponse.from(response));
	}

	@PutMapping("/rooms/{roomId}")
	@Operation(
		summary = "장소투표방 업데이트하기",
		description = """
			장소후보를 리스트로 입력을 받아서 장소투표방을 업데이트한다.
			
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
				description = "존재하지 않는 방입니다.[R-201]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "409",
				description = "이미 투표방이 존재합니다.[V-302]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<Void>> placeVoteRoomUpdate(
		@PathVariable("roomId") Long roomId,
		@RequestBody @Valid PlaceVoteRoomCreateRequest request
	) {
		Member member = memberLoader.getMember();

		placeVoteRoomService.UpdatePlaceVoteRoom(member.getId(), roomId, request);

		return ResponseEntity.ok(DataResponse.ok());
	}
}
