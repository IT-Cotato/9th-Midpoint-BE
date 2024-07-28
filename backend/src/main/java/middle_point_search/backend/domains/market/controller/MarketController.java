package middle_point_search.backend.domains.market.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.dto.BaseResponse;
import middle_point_search.backend.common.dto.DataResponse;
import middle_point_search.backend.common.dto.ErrorResponse;
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
	@Operation(
		summary = "중간 장소 리스트 업데이트",
		description = """
			중간 지점으로 선정될 장소를 업데이트 한다.
			
			AccessToken 필요(ADMIN 권한 필요.)"""
	)
	public ResponseEntity<BaseResponse> marketUpdate() {
		marketService.updateMarket();

		return ResponseEntity
			.status(HttpStatus.OK)
			.body(BaseResponse.ok());
	}

	@GetMapping("recommend-places")
	@Operation(
		summary = "중간 지점 근처 식당, 스터디, 카페 추천",
		description = """
			주소, 카테고리, 페이지 정보를 이용해 장소를 추천한다.
			
			placeStandard에는 [ALL,CAFE,STUDY,RESTAURANT]중 하나가 올 수 있다.
			
			page는 1이상이다.
			
			요청당 5개의 정보를 반환.
			
			AccessToken 필요.""",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
			),
			@ApiResponse(
				responseCode = "400",
				description = "요청 파라미터가 잘 못 되었습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "401",
				description = "인증에 실패하였습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "403",
				description = "접근이 거부되었습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<Page<RecommendPlacesFindResponse>>> recommendPlacesFind(
		@Valid @ModelAttribute RecommendPlacesFindRequest request) {

		Page<RecommendPlacesFindResponse> recommendPlaces = marketService.findRecommendPlaces(request);

		return ResponseEntity.ok(DataResponse.from(recommendPlaces));
	}
}
