package middle_point_search.backend.domains.market.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.dto.BaseResponse;
import middle_point_search.backend.domains.market.service.MarketService;

@Tag(name = "MARKET API", description = "상권에 대한 API입니다.")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MarketController {

	private final MarketService marketService;

	@PostMapping("/market")
	@Operation(
		summary = "중간 장소 리스트 업데이트",
		parameters = {
			@Parameter(name = "RoomId", description = "roomId 필요", in = ParameterIn.HEADER),
			@Parameter(name = "RoomType", description = "roomType 필요. [TOGETHER, SELF] 중 하나", in = ParameterIn.HEADER)
		},
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
}
