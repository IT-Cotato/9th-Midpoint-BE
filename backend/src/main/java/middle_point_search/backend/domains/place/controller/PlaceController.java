package middle_point_search.backend.domains.place.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
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
import middle_point_search.backend.domains.place.dto.request.SavePlaceRequest;
import middle_point_search.backend.domains.place.dto.request.UpdatePlaceRequest;
import middle_point_search.backend.domains.place.dto.response.FindPlacesResponse;
import middle_point_search.backend.domains.place.dto.response.SavePlaceResponse;
import middle_point_search.backend.domains.place.service.PlaceService;

@Tag(name = "PLACE API", description = "회원 장소에 대한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/places")
public class PlaceController {

	private final PlaceService placeService;
	private final MemberLoader memberLoader;

	@PostMapping("/rooms/{roomId}")
	@Operation(
		summary = "장소 저장하기",
		description = """
			주소와 좌표를 사용하여 장소 저장
			
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
				description = """
					인증에 실패하였습니다.[C-101] \n
					Refresh Token이 유효하지 않습니다.[A-003]""",
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
			)
		}
	)
	public ResponseEntity<DataResponse<SavePlaceResponse>> savePlace(
		@PathVariable("roomId") String roomId,
		@RequestBody @Valid SavePlaceRequest request
	) {
		Member member = memberLoader.getMember();

		SavePlaceResponse response = placeService.savePlace(roomId, member, request);

		return ResponseEntity.ok(DataResponse.from(response));
	}

	@PatchMapping("/rooms/{roomId}")
	@Operation(
		summary = "장소 변경하기",
		description = """
			placeId, 주소와 좌표를 사용하여 장소 변경
			
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
				description = """
					인증에 실패하였습니다.[C-101] \n
					Refresh Token이 유효하지 않습니다.[A-003]""",
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
			)
		}
	)
	public ResponseEntity<DataResponse<Void>> updatePlace(
		@PathVariable("roomId") String roomId,
		@RequestBody @Valid UpdatePlaceRequest request
	) {
		Member member = memberLoader.getMember();

		placeService.updatePlace(roomId, member, request);

		return ResponseEntity.ok(DataResponse.ok());
	}

	@GetMapping("/rooms/{roomId}")
	@Operation(
		summary = "장소 조회하기",
		description = """
			저장한 장소들 조회하기.
			
			내가 저장한 장소들과 다른 회원이 저장한 장소들을 모두 조회한다.
			
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
				description = """
					인증에 실패하였습니다.[C-101] \n
					Refresh Token이 유효하지 않습니다.[A-003]""",
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
		}
	)
	public ResponseEntity<DataResponse<FindPlacesResponse>> findPlaces(
		@PathVariable("roomId") String roomId
	) {
		Long memberId = memberLoader.getMemberId();

		FindPlacesResponse response = placeService.findPlaces(memberId, roomId);

		return ResponseEntity.ok(DataResponse.from(response));
	}

	@DeleteMapping("/{placeId}")
	@Operation(
		summary = "장소 삭제하기",
		description = """
			저장한 장소 삭제하기.
			
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
				description = """
					인증에 실패하였습니다.[C-101] \n
					Refresh Token이 유효하지 않습니다.[A-003]""",
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
			)
		}
	)
	public ResponseEntity<DataResponse<Void>> deletePlace(
		@PathVariable("placeId") Long placeId
	) {
		Long memberId = memberLoader.getMemberId();

		placeService.deletePlace(memberId, placeId);

		return ResponseEntity.ok(DataResponse.ok());
	}
}

