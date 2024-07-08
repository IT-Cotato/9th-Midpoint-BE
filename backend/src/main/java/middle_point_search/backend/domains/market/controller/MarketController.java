package middle_point_search.backend.domains.market.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.dto.BaseResponse;
import middle_point_search.backend.common.dto.DataResponse;
import middle_point_search.backend.domains.market.dto.request.RecommendPlacesFindRequest;
import middle_point_search.backend.domains.market.dto.response.RecommendPlacesFindResponse;
import middle_point_search.backend.domains.market.service.MarketService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MarketController {

	private final MarketService marketService;

	@PostMapping("/market")
	public ResponseEntity<BaseResponse> marketUpdate() {
		marketService.updateMarket();

		return ResponseEntity
			.status(HttpStatus.OK)
			.body(BaseResponse.ok());
	}

	@GetMapping("recommend-places")
	public ResponseEntity<DataResponse<Page<RecommendPlacesFindResponse>>> recommendPlacesFind(@Valid @ModelAttribute RecommendPlacesFindRequest request) {

		Page<RecommendPlacesFindResponse> recommendPlaces = marketService.findRecommendPlaces(request);

		return ResponseEntity.ok(DataResponse.from(recommendPlaces));
	}
}
