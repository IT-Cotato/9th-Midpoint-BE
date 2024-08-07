package middle_point_search.backend.domains.room.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.dto.DataResponse;
import middle_point_search.backend.common.dto.ErrorResponse;
import middle_point_search.backend.common.util.MemberLoader;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.dto.RoomDTO;
import middle_point_search.backend.domains.room.dto.RoomDTO.RoomCreateResponse;
import middle_point_search.backend.domains.room.dto.RoomDTO.RoomExistenceCheckResponse;
import middle_point_search.backend.domains.room.service.RoomService;

@Tag(name = "ROOM API",  description = "방에 대한 API입니다.")
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

	private final RoomService roomService;
	private final MemberLoader memberLoader;

	@PostMapping
	@Operation(
		summary = "방 생성하기",
		description = "방을 생성한다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
			),
			@ApiResponse(
				responseCode = "429",
				description = "요청을 너무 많이 했습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<RoomCreateResponse>> roomCreate() {
		RoomCreateResponse response = roomService.createRoom();

		return ResponseEntity.ok(DataResponse.from(response));
	}

	@GetMapping("/{roomId}/existence")
	@Operation(
		summary = "방 존재 확인하기",
		description = "방 id를 통해 존재하는 방인지 확인한다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
			)
		}
	)
	public ResponseEntity<DataResponse<RoomExistenceCheckResponse>> roomExistenceCheck(
		@PathVariable("roomId") String identityNumber) {
		RoomExistenceCheckResponse response = roomService.checkRoomExistence(identityNumber);

		return ResponseEntity.ok(DataResponse.from(response));
	}

	@GetMapping("/room-name")
	@Operation(
		summary = "방 이름 조회",
		description = """
			accessToken을 통해 자신의 방이름을 확인한다.
						
			accessToken 필요.""",
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
			)
		}
	)
	public ResponseEntity<DataResponse<RoomDTO.RoomNameResponse>> roomNameFind() {
		Room room = memberLoader.getRoom();

		RoomDTO.RoomNameResponse response = roomService.findRoomName(room);

		return ResponseEntity.ok(DataResponse.from(response));
	}
}


