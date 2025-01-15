package middle_point_search.backend.domains.memberRoom.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.memberRoom.dto.MemberRoomDTO.MemberRoomExistsResponse;
import middle_point_search.backend.domains.memberRoom.dto.MemberRoomDTO.RoomsByMemberIdFindResponse;
import middle_point_search.backend.domains.memberRoom.service.MemberRoomService;

@Tag(name = "MEMBER_ROOM API", description = "회원방에 대한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member-rooms")
public class MemberRoomController {

	private final MemberRoomService memberRoomService;
	private final MemberLoader memberLoader;

	@PostMapping("/rooms/{roomId}")
	@Operation(
		summary = "회원을 방에 저장",
		description = "회원을 방에 저장합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
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
				responseCode = "404",
				description = "존재하지 않는 방입니다.[R-201]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "409",
				description = "해당 방에 이미 존재하는 회원입니다.[MR-002]",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<DataResponse<Void>> saveMemberToRoom(@PathVariable("roomId") String roomId) {
		Member member = memberLoader.getMember();

		memberRoomService.saveMemberToRoom(member, roomId);

		return ResponseEntity.ok(DataResponse.ok());
	}

	@GetMapping
	@Operation(
		summary = "회원이 속한 방들 조회",
		description = """
			회원이 속한 방들을 조회합니다.
			true면 존재, false면 존재하지 않음""",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
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
		}
	)
	public ResponseEntity<DataResponse<List<RoomsByMemberIdFindResponse>>> findRooms() {
		Long memberId = memberLoader.getMemberId();

		List<RoomsByMemberIdFindResponse> responses = memberRoomService.findRooms(memberId);

		return ResponseEntity.ok(DataResponse.from(responses));
	}

	@GetMapping("/exists/rooms/{roomId}")
	@Operation(
		summary = "회원방 존재 여부 조회",
		description = "회원방 존재 여부를 조회합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공"
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
		}
	)
	public ResponseEntity<DataResponse<MemberRoomExistsResponse>> existsMemberRoom(
		@PathVariable("roomId") String roomId
	) {
		Long memberId = memberLoader.getMemberId();

		MemberRoomExistsResponse response = memberRoomService.existsMemberRoom(memberId, roomId);

		return ResponseEntity.ok(DataResponse.from(response));
	}
}
