package middle_point_search.backend.domains.place.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.dto.DataResponse;
import middle_point_search.backend.common.dto.ErrorResponse;
import middle_point_search.backend.common.util.MemberLoader;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlaceSaveOrUpdateRequest;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlacesFindResponse;
import middle_point_search.backend.domains.place.service.PlaceService;

@Tag(name = "PLACE API", description = "회원 장소에 대한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/place-rooms")
public class PlaceController {

	private final PlaceService placeService;
	private final MemberLoader memberLoader;

	@PostMapping("/rooms/{roomId}")
	@Operation(
		summary = "장소 저장하기",
		description = """
			주소와 좌표를 사용하여 장소 저장
			
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
				responseCode = "401",
				description = "인증에 실패하였습니다."
			),
			@ApiResponse(
				responseCode = "402",
				description = "인증 토큰이 유효하지 않습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "422",
				description = "방의 타입이 일치하지 않습니다",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<Void>> placeSave(
		@PathVariable("roomId") Long roomId,
		@RequestBody @Valid PlaceSaveOrUpdateRequest request
	) {
		Member member = memberLoader.getMember();

		placeService.savePlace(roomId, member, request);

		return ResponseEntity.ok(DataResponse.ok());
	}

	@GetMapping("/rooms/{roomId}")
	@Operation(
		summary = "장소 조회하기",
		description = """
			저장한 장소들 조회하기.
			
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
				description = "요청 파라미터가 잘 못 되었습니다."
			),
			@ApiResponse(
				responseCode = "401",
				description = "인증에 실패하였습니다."
			),
			@ApiResponse(
				responseCode = "402",
				description = "인증 토큰이 유효하지 않습니다.",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "422",
				description = "방의 타입이 일치하지 않습니다",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<PlacesFindResponse>> placesFind(
		@PathVariable("roomId") Long roomId
	) {
		PlacesFindResponse response = placeService.findPlaces(roomId);

		return ResponseEntity.ok(DataResponse.from(response));
	}

	@DeleteMapping("/rooms/{roomId}")
	public ResponseEntity<DataResponse<Void>> placeDelete(
		@PathVariable("roomId") Long roomId
	) {
		placeService.deletePlace(roomId);

		return ResponseEntity.ok(DataResponse.ok());
	}
}
