package middle_point_search.backend.domains.room.controller;

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
import middle_point_search.backend.domains.room.dto.RoomDTO.RoomCreateRequest;
import middle_point_search.backend.domains.room.dto.RoomDTO.RoomCreateResponse;
import middle_point_search.backend.domains.room.dto.RoomDTO.RoomNameUpdateRequest;
import middle_point_search.backend.domains.room.service.RoomService;

@Tag(name = "ROOM API", description = "방에 대한 API입니다.")
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

	private final RoomService roomService;
	private final MemberLoader memberLoader;

	@PostMapping
	@Operation(
		summary = "방 생성하기",
		description = """
			방을 생성한다.
			""",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
			),
			@ApiResponse(
				responseCode = "400",
				description = "[요청이 잘 못 되었습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "429",
				description = "요청을 너무 많이 했습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<RoomCreateResponse>> roomCreate(@RequestBody @Valid RoomCreateRequest request) {
		RoomCreateResponse response = roomService.createRoom(request);

		return ResponseEntity.ok(DataResponse.from(response));
	}

	@PutMapping("/{roomId}")
	@Operation(
		summary = "방 이름 변경",
		description = """
			방 이름을 변경한다.
			
			accessToken 필요.""",
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
		}
	)
	public ResponseEntity<DataResponse<Void>> roomNameUpdate(@PathVariable Long roomId, @RequestBody RoomNameUpdateRequest request) {
		roomService.updateRoomName(roomId, request);

		return ResponseEntity.ok(DataResponse.ok());
	}
}


