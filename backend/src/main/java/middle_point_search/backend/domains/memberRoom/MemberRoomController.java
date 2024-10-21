package middle_point_search.backend.domains.memberRoom;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.util.MemberLoader;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.memberRoom.MemberRoomDTO.MemberToRoomSaveRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member-rooms")
public class MemberRoomController {

	private final MemberRoomService memberRoomService;
	private final MemberLoader memberLoader;

	@PostMapping
	public void saveMemberToRoom(@RequestBody @Valid MemberToRoomSaveRequest request) {
		Member member = memberLoader.getMember();

		memberRoomService.saveMemberToRoom(member, request);
	}
}
