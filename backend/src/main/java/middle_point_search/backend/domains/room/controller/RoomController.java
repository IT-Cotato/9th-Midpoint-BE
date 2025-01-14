package middle_point_search.backend.domains.room.controller;

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
import middle_point_search.backend.domains.room.dto.RoomDTO.RoomCreateRequest;
import middle_point_search.backend.domains.room.dto.RoomDTO.RoomCreateResponse;
import middle_point_search.backend.domains.room.dto.RoomDTO.RoomExistResponse;
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
			메모가 없을 경우 필드를 제거하거나 ""을 보내면 된다.
			
			방 생성시 회원을 방에 등록시켜줘야 한다.
			""",
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
				responseCode = "429",
				description = "요청을 너무 많이 했습니다.[C-203]",
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
		}
	)
	public ResponseEntity<DataResponse<Void>> roomNameUpdate(
		@PathVariable String roomId,
		@RequestBody RoomNameUpdateRequest request
	) {
		Long memberId = memberLoader.getMemberId();

		roomService.updateRoomName(memberId, roomId, request);

		return ResponseEntity.ok(DataResponse.ok());
	}

	@GetMapping("/{roomId}/existence")
	@Operation(
		summary = "방 존재확인",
		description = """
			방이 존재하는 지 조회한다.
			
			accessToken 필요.""",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
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
				responseCode = "404",
				description = "존재하지 않는 방입니다.[R-201]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
		}
	)
	public ResponseEntity<DataResponse<RoomExistResponse>> roomExist(@PathVariable String roomId) {
		RoomExistResponse response = roomService.existRoom(roomId);

		return ResponseEntity.ok(DataResponse.from(response));
	}
}


