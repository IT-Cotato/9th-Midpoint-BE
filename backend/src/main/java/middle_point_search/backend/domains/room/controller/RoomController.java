package middle_point_search.backend.domains.room.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.dto.DataResponse;
import middle_point_search.backend.domains.room.dto.RoomDTO.RoomCreateResponse;
import middle_point_search.backend.domains.room.dto.RoomDTO.RoomExistenceCheckResponse;
import middle_point_search.backend.domains.room.service.RoomService;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {
	private final RoomService roomService;

	@PostMapping
	@Operation(
		summary = "방 생성하기",
		description = "방을 생성한다."
	)
	public ResponseEntity<DataResponse<RoomCreateResponse>> roomCreate() {
		RoomCreateResponse response = roomService.createRoom();

		return ResponseEntity.ok(DataResponse.from(response));
	}

	@GetMapping("/{roomId}/existence")
	@Operation(
		summary = "방 존재 확인하기",
		description = "방 id를 통해 존재하는 방인지 확인한다."
	)
	public ResponseEntity<DataResponse<RoomExistenceCheckResponse>> roomExistenceCheck(
		@PathVariable("roomId") String identityNumber) {
		RoomExistenceCheckResponse response = roomService.checkRoomExistence(identityNumber);

		return ResponseEntity.ok(DataResponse.from(response));
	}
}
