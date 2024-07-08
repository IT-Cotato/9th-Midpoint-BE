package middle_point_search.backend.domains.place.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.dto.BaseResponse;
import middle_point_search.backend.common.dto.DataResponse;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlaceSaveRequest;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlacesFindResponse;
import middle_point_search.backend.domains.place.service.PlaceService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/place-rooms")
public class PlaceController {

	private final PlaceService placeService;

	@PostMapping
	public ResponseEntity<BaseResponse> placeSave(@RequestBody PlaceSaveRequest placeSaveRequest) {

		placeService.savePlace(placeSaveRequest);

		return ResponseEntity.ok(BaseResponse.ok());
	}

	@GetMapping("/{roomId}")
	public ResponseEntity<DataResponse<List<PlacesFindResponse>>> placesFind(@PathVariable("roomId") String roomId) {

		List<PlacesFindResponse> places = placeService.findPlaces(roomId);

		return ResponseEntity.ok(DataResponse.from(places));
	}
}
