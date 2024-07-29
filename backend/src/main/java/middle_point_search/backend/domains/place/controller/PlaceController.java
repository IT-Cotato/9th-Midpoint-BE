package middle_point_search.backend.domains.place.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.dto.BaseResponse;
import middle_point_search.backend.common.dto.DataResponse;
import middle_point_search.backend.common.util.MemberLoader;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlaceSaveOrUpdateRequest;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlacesFindResponse;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlacesSaveOrUpdateBySelfRequest;
import middle_point_search.backend.domains.place.service.PlaceService;
import middle_point_search.backend.domains.room.domain.Room;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/place-rooms")
public class PlaceController {

	private final PlaceService placeService;
	private final MemberLoader memberLoader;

	@PostMapping
	@Operation(
		summary = "각자 장소 저장 및 업데이트하기",
		description = """
			주소와 좌표를 사용하여 장소 저장 및 업데이트하기.
						
			저장된 장소가 없으면 저장, 있으면 업데이트 한다.
						
			장소를 저장한 사람은 다른 기능을 사용할 권한이 생긴다.
						
						
						
			AccessToken 필요.""",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
			),
			@ApiResponse(
				responseCode = "400",
				description = """
					요청 파라미터가 잘 못 되었습니다.
										
					이미 개개인 입력으로 저장한 상태에서 이 api 요청시 400에러 반환"""
			),
			@ApiResponse(
				responseCode = "401",
				description = "인증에 실패하였습니다."
			)
		}
	)
	public ResponseEntity<BaseResponse> placeSaveOrUpdate(@RequestBody @Valid PlaceSaveOrUpdateRequest request) {

		Room room = memberLoader.getRoom();
		Member member = memberLoader.getMember();

		placeService.saveOrUpdatePlaceAndRoleUpdate(room, member, request);

		return ResponseEntity.ok(BaseResponse.ok());
	}

	@PostMapping("/self")
	@Operation(
		summary = "개인이 모든 장소 저장 및 업데이트하기",
		description = """
			주소와 좌표를 사용하여 장소 저장 및 업데이트하기.
						
			저장된 장소가 없으면 저장, 있으면 업데이트 한다.
						
			추후 가입하는 모든 사람은 다른 기능을 사용할 권한이 생긴다.
						
			AccessToken 필요.""",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
			),
			@ApiResponse(
				responseCode = "400",
				description = """
					요청 파라미터가 잘 못 되었습니다.
										
					이미 개인 입력으로 저장한 상태에서 이 api 요청시 400에러 반환"""
			),
			@ApiResponse(
				responseCode = "401",
				description = "인증에 실패하였습니다."
			)
		}
	)
	public ResponseEntity<BaseResponse> placesSaveOrUpdateBySelf(@RequestBody PlacesSaveOrUpdateBySelfRequest request) {

		Room room = memberLoader.getRoom();

		placeService.saveOrUpdatePlacesBySelfAndRoleUpdate(room, request);

		return ResponseEntity.ok(BaseResponse.ok());
	}

	@GetMapping
	@Operation(
		summary = "장소 조회하기",
		description = """
			저장된 장소 리스트 조회하기.
						
			AccessToken 필요.""",
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
			)
		}
	)
	public ResponseEntity<DataResponse<List<PlacesFindResponse>>> placesFind() {
		String roomId = memberLoader.getRoomId();

		List<PlacesFindResponse> places = placeService.findPlaces(roomId);

		return ResponseEntity.ok(DataResponse.from(places));
	}

}
