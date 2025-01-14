package middle_point_search.backend.domains.midPoint.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.dto.DataResponse;
import middle_point_search.backend.common.dto.ErrorResponse;
import middle_point_search.backend.common.util.MemberLoader;
import middle_point_search.backend.domains.midPoint.dto.MidPointDTO.MidPointsFindResponse;
import middle_point_search.backend.domains.midPoint.dto.MidPointDTO.TravelTimesFindResponse;
import middle_point_search.backend.domains.midPoint.service.MidPointService;

@Tag(name = "MID POINT API", description = "중간지점에 대한 API입니다.")
@RestController
@RequestMapping("/api/mid-points")
@RequiredArgsConstructor
public class MidPointController {

	private final MidPointService midPointService;
	private final MemberLoader memberLoader;

	@GetMapping("/rooms/{roomId}")
	@Operation(
		summary = "중간 지점 추천 장소 조회",
		description = """
			중간 지점 추천 장소 조회하기.
			
			AccessToken 필요.""",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
			),
			@ApiResponse(
				responseCode = "400",
				description = "요청 파라미터가 잘못되었습니다.[C-202]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "401",
				description = "인증에 실패하였습니다.[C-101]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "402",
				description = "Access Token을 재발급해야합니다.[A-004]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "403",
				description = "해당 방의 회원이 아닙니다.[MR-003]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "404",
				description = "방에 입력된 장소가 없습니다.[P-201]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<List<MidPointsFindResponse>>> MidPointsFind(
		@PathVariable("roomId") String roomId
	) {
		Long memberId = memberLoader.getMemberId();

		List<MidPointsFindResponse> midPoints = midPointService.findMidPointsByRoomId(memberId, roomId);

		return ResponseEntity.ok(DataResponse.from(midPoints));
	}

	@GetMapping("/rooms/{roomId}/travel-time")
	@Operation(
		summary = "중간 지점까지의 이동 시간 조회",
		description = """
			중간 지점까지의 이동 시간 조회하기.
			
			AccessToken 필요.""",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
			),
			@ApiResponse(
				responseCode = "400",
				description = "잘못된 요청입니다.(외부 API 실패) [C-201]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "401",
				description = "인증에 실패하였습니다.[C-101]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "402",
				description = "Access Token을 재발급해야합니다.[A-004]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "403",
				description = "해당 방의 회원이 아닙니다.[MR-003]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "404",
				description = "방에 입력된 장소가 없습니다.[P-201]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "500",
				description = "API 서버에 문제가 발생하였습니다.[S-001]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<TravelTimesFindResponse>> findPath(
		@PathVariable String roomId,
		@RequestParam Double destLatitude,
		@RequestParam Double destLongitude
	) {
		final Long memberId = memberLoader.getMemberId();

		return ResponseEntity.ok(DataResponse.from(midPointService.findTravelTimes(
			roomId,
			memberId,
			destLatitude,
			destLongitude)));
	}
}
