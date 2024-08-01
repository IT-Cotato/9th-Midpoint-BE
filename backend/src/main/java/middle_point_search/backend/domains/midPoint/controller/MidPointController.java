package middle_point_search.backend.domains.midPoint.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
import middle_point_search.backend.common.dto.DataResponse;
import middle_point_search.backend.common.dto.ErrorResponse;
import middle_point_search.backend.common.util.MemberLoader;
import middle_point_search.backend.domains.midPoint.dto.MidPointDTO.MidPointsFindResponse;
import middle_point_search.backend.domains.midPoint.service.MidPointService;

@Tag(name = "MID POINT API", description = "중간지점에 대한 API입니다.")
@RestController
@RequestMapping("/api/mid-points")
@RequiredArgsConstructor
public class MidPointController {

	private final MemberLoader memberLoader;
	private final MidPointService midPointService;

	@GetMapping
	@Operation(
		summary = "중간 지점 추천 장소 조회",
		description = """
			중간 지점 추천 장소 조회하기.
			
			AccessToken 필요.""",
		parameters = {
			@Parameter(name = "RoomId", description = "roomId 필요", required = true, in = ParameterIn.HEADER)
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
				responseCode = "404",
				description = "방에 입력된 장소가 없습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<List<MidPointsFindResponse>>> MidPointsFind() {
		String roomId = memberLoader.getRoomId();

		List<MidPointsFindResponse> midPoints = midPointService.findMidPointsByRoomId(roomId);

		return ResponseEntity.ok(DataResponse.from(midPoints));
	}
}
