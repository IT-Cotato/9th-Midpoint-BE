package middle_point_search.backend.domains.recommendPlace.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.dto.DataResponse;
import middle_point_search.backend.common.dto.ErrorResponse;
import middle_point_search.backend.domains.market.dto.request.RecommendPlacesFindRequest;
import middle_point_search.backend.domains.market.dto.response.RecommendPlacesFindResponse;
import middle_point_search.backend.domains.recommendPlace.service.RecommendPlaceService;

@Tag(name = "RECOMMEND PLACE API",  description = "추천 장소에 대한 API입니다.")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RecommendPlaceController {

	private final RecommendPlaceService recommendPlaceService;

	@GetMapping("/recommend-places")
	@Operation(
		summary = "중간 지점 근처 식당, 스터디, 카페 추천",
		description = """
			주소, 카테고리, 페이지 정보를 이용해 장소를 추천한다.
						
			page는 1이상이다.
						
			요청당 5개의 정보를 반환.
						
			AccessToken 필요.""",
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
			),
			@ApiResponse(
				responseCode = "422",
				description = "방의 타입이 일치하지 않습니다"
			)
		}
	)
	public ResponseEntity<DataResponse<Page<RecommendPlacesFindResponse>>> recommendPlacesFind(
		@ModelAttribute @ParameterObject RecommendPlacesFindRequest request) {

		Page<RecommendPlacesFindResponse> recommendPlaces = recommendPlaceService.findRecommendPlaces(request);

		return ResponseEntity.ok(DataResponse.from(recommendPlaces));
	}
}
