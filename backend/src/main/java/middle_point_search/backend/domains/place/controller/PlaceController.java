package middle_point_search.backend.domains.place.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.dto.BaseResponse;
import middle_point_search.backend.common.dto.DataResponse;
import middle_point_search.backend.common.util.MemberLoader;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlaceFindResponse;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlaceSaveOrUpdateRequest;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlacesFindResponse;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlacesSaveOrUpdateBySelfRequest;
import middle_point_search.backend.domains.place.service.PlaceService;
import middle_point_search.backend.domains.room.domain.Room;

@Tag(name = "PLACE API", description = "회원 장소에 대한 API입니다.")
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
				responseCode = "422",
				description = "방의 타입이 일치하지 않습니다"
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
				responseCode = "422",
				description = "방의 타입이 일치하지 않습니다"
			)
		}
	)
	public ResponseEntity<BaseResponse> placesSaveOrUpdateBySelf(@RequestBody @Valid PlacesSaveOrUpdateBySelfRequest request) {

		Room room = memberLoader.getRoom();

		placeService.saveOrUpdatePlacesBySelfAndRoleUpdate(room, request);

		return ResponseEntity.ok(BaseResponse.ok());
	}

	@GetMapping("/self")
	@Operation(
		summary = "개개인 장소 조회하기",
		description = """
			개개인이 저장한 장소 및 다른 사람들이 저장한 장소들 조회하기.
			
			다른 사람들이 저장한 장소(otherPlaces)에는 당사자가 저장한 장소는 포함되지 않는다.
						
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
				responseCode = "422",
				description = "방의 타입이 일치하지 않습니다"
			)
		}
	)
	public ResponseEntity<DataResponse<PlaceFindResponse>> placeFind() {
		String roomId = memberLoader.getRoomId();
		String memberName = memberLoader.getName();

		PlaceFindResponse response = placeService.findPlace(roomId, memberName);

		return ResponseEntity.ok(DataResponse.from(response));
	}

	@GetMapping
	@Operation(
		summary = "개인 장소 조회하기",
		description = """
			저장된 장소 리스트 조회하기.
						
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
				responseCode = "422",
				description = "방의 타입이 일치하지 않습니다"
			)
		}
	)
	public ResponseEntity<DataResponse<PlacesFindResponse>> placesFind() {
		String roomId = memberLoader.getRoomId();

		PlacesFindResponse response = placeService.findPlaces(roomId);

		return ResponseEntity.ok(DataResponse.from(response));
	}

}
