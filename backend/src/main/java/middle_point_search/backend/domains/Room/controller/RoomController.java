package middle_point_search.backend.domains.Room.controller;

import static middle_point_search.backend.domains.Room.dto.RoomDTO.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.dto.DataResponse;
import middle_point_search.backend.domains.Room.service.RoomService;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {
	private final RoomService roomService;

	@PostMapping
	public ResponseEntity<DataResponse<RoomCreateResponse>> roomCreate() {

		return ResponseEntity.ok(DataResponse.from(roomService.createRoom()));
	}
}
