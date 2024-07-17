package middle_point_search.backend.domains.place.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.dto.BaseResponse;
import middle_point_search.backend.common.dto.DataResponse;
import middle_point_search.backend.common.util.MemberLoader;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.domain.Role;
import middle_point_search.backend.domains.member.service.MemberService;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlaceSaveRequest;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlaceUpdateRequest;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlacesFindResponse;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlacesSaveBySelfRequest;
import middle_point_search.backend.domains.place.service.PlaceService;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.domain.RoomType;
import middle_point_search.backend.domains.room.service.RoomService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/place-rooms")
public class PlaceController {

	private final PlaceService placeService;
	private final MemberService memberService;
	private final MemberLoader memberLoader;
	private final RoomService roomService;

	@PostMapping
	@Operation(
		summary = "각자 장소 저장하기",
		description = "주소와 좌표를 사용하여 장소 저장하기. 장소를 저장한 사람은 다른 기능을 사용할 권한이 생긴다."
	)
	public ResponseEntity<BaseResponse> placeSave(@RequestBody PlaceSaveRequest request) {

		Room room = memberLoader.getRoom();
		Member member = memberLoader.getMember();
		String roomId = memberLoader.getRoomId();

		roomService.updateRoomType(room, RoomType.TOGETHER);
		placeService.savePlace(room, member, request);
		memberService.updateRomeMembersRole(roomId, Role.USER);

		return ResponseEntity.ok(BaseResponse.ok());
	}

	@PostMapping("/self")
	@Operation(
		summary = "개인이 모든 장소 저장하기",
		description = "주소와 좌표를 사용하여 장소 저장하기. 추후 가입하는 모든 사람은 다른 기능을 사용할 권한이 생긴다."
	)
	public ResponseEntity<BaseResponse> placesSaveBySelf(@RequestBody PlacesSaveBySelfRequest request) {

		Room room = memberLoader.getRoom();
		Member member = memberLoader.getMember();

		roomService.updateRoomType(room, RoomType.SELF);
		placeService.savePlacesBySelf(room, request);
		memberService.updateMemberRole(member, Role.USER);

		return ResponseEntity.ok(BaseResponse.ok());
	}

	@PutMapping("/{placeId}")
	@Operation(
		summary = "장소 변경하기",
		description = "주소와 좌표를 사용하여 장소 변경하기"
	)
	public ResponseEntity<BaseResponse> placeUpdate(@PathVariable("placeId") Long placeId,
		@RequestBody PlaceUpdateRequest request) {

		placeService.updatePlaces(placeId, request);

		return ResponseEntity.ok(BaseResponse.ok());
	}

	@GetMapping
	@Operation(
		summary = "장소 조회하기",
		description = "저장된 장소 리스트를 조회하기. AccessToken 필요"
	)
	public ResponseEntity<DataResponse<List<PlacesFindResponse>>> placesFind() {
		String roomId = memberLoader.getRoomId();

		List<PlacesFindResponse> places = placeService.findPlaces(roomId);

		return ResponseEntity.ok(DataResponse.from(places));
	}

	@DeleteMapping("/{placeId}")
	@Operation(
		summary = "장소 삭제하기",
		description = "저장된 장소 삭제하기. AccessToken 필요"
	)
	public ResponseEntity<BaseResponse> placeDelete(@PathVariable Long placeId) {

		placeService.deletePlace(placeId);

		return ResponseEntity.ok(BaseResponse.ok());
	}
}
