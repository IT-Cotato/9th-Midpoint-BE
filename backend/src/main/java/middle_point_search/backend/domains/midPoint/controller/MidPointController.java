package middle_point_search.backend.domains.midPoint.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.dto.DataResponse;
import middle_point_search.backend.common.util.MemberLoader;
import middle_point_search.backend.domains.midPoint.dto.MidPointDTO.MidPointsBySelfFindRequest;
import middle_point_search.backend.domains.midPoint.dto.MidPointDTO.MidPointsFindResponse;
import middle_point_search.backend.domains.midPoint.service.MidPointService;

@RestController
@RequestMapping("/api/mid-points")
@RequiredArgsConstructor
public class MidPointController {

	private final MemberLoader memberLoader;
	private final MidPointService midPointService;

	@PostMapping("/self")
	public ResponseEntity<DataResponse<List<MidPointsFindResponse>>> MidPointsBySelfFind(
		@RequestBody MidPointsBySelfFindRequest request) {
		List<MidPointsFindResponse> midPoints = midPointService.findMidPoints(request.getAddresses());

		return ResponseEntity.ok(DataResponse.from(midPoints));
	}

	@GetMapping
	public ResponseEntity<DataResponse<List<MidPointsFindResponse>>> MidPointsFind() {
		String roomId = memberLoader.getRoomId();

		List<MidPointsFindResponse> midPoints = midPointService.findMidPointsByRoomId(roomId);

		return ResponseEntity.ok(DataResponse.from(midPoints));
	}
}
