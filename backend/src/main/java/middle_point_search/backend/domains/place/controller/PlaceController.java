package middle_point_search.backend.domains.place.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.dto.BaseResponse;
import middle_point_search.backend.common.dto.DataResponse;
import middle_point_search.backend.common.util.MemberLoader;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlaceSaveRequest;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlaceUpdateRequest;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlacesFindResponse;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlacesSaveBySelfRequest;
import middle_point_search.backend.domains.place.service.PlaceService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/place-rooms")
public class PlaceController {

	private final PlaceService placeService;
	private final MemberLoader memberLoader;

	@PostMapping
	public ResponseEntity<BaseResponse> placeSave(@RequestBody PlaceSaveRequest request) {

		placeService.savePlace(request);

		return ResponseEntity.ok(BaseResponse.ok());
	}

	@PostMapping("/self")
	public ResponseEntity<BaseResponse> placesSaveBySelf(@RequestBody PlacesSaveBySelfRequest request) {

		placeService.savePlacesBySelf(request);

		return ResponseEntity.ok(BaseResponse.ok());
	}

	@PutMapping("/{placeId}")
	public ResponseEntity<BaseResponse> placeUpdate(@PathVariable("placeId") Long placeId,
		@RequestBody PlaceUpdateRequest request) {

		placeService.updatePlaces(placeId, request);

		return ResponseEntity.ok(BaseResponse.ok());
	}

	@GetMapping
	public ResponseEntity<DataResponse<List<PlacesFindResponse>>> placesFind() {
		String roomId = memberLoader.getRoomId();

		List<PlacesFindResponse> places = placeService.findPlaces(roomId);

		return ResponseEntity.ok(DataResponse.from(places));
	}

	@DeleteMapping("/{placeId}")
	public ResponseEntity<BaseResponse> placeDelete(@PathVariable Long placeId) {

		placeService.deletePlace(placeId);

		return ResponseEntity.ok(BaseResponse.ok());
	}
}
