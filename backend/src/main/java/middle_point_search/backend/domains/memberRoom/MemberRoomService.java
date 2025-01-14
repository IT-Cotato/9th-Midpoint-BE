package middle_point_search.backend.domains.memberRoom;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.memberRoom.MemberRoomDTO.MemberRoomExistsResponse;
import middle_point_search.backend.domains.memberRoom.MemberRoomDTO.RoomsByMemberIdFindResponse;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.service.RoomService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberRoomService {

	private final MemberRoomRepository memberRoomRepository;
	private final RoomService roomService;
	private final MemberRoomValidateService memberRoomValidateService;

	// 회원방을 DTO로 저장
	@Transactional(rollbackFor = CustomException.class)
	public void saveMemberToRoom(Member member, String roomId) {
		// 방조회
		Room room = roomService.findRoom(roomId)
			.orElseThrow(() -> CustomException.from(ROOM_NOT_FOUND));

		// 중복 확인
		memberRoomValidateService.validateDuplicatedMemberRoom(member, room);

		// 회원방 저장
		MemberRoom memberRoom = MemberRoom.builder()
			.member(member)
			.room(room)
			.build();

		memberRoomRepository.save(memberRoom);
	}

	// 회원이 속한 방들을 DTO로 조회
	public List<RoomsByMemberIdFindResponse> findRooms(Long memberId) {
		List<MemberRoom> memberRooms = memberRoomRepository.findByMember_Id(memberId);

		return memberRooms.stream()
			.map(memberRoom -> RoomsByMemberIdFindResponse.from(memberRoom.getRoom()))
			.toList();
	}

	// 회원방이 존재하는지 확인
	public MemberRoomExistsResponse existsMemberRoom(Long memberId, String roomId) {
		Boolean exists = memberRoomRepository.existsByMember_IdAndRoom_Id(memberId, roomId);

		return MemberRoomExistsResponse.from(exists);
	}
}
