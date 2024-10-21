package middle_point_search.backend.domains.memberRoom;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.memberRoom.MemberRoomDTO.MemberToRoomSaveRequest;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.service.RoomService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberRoomService {

	private final MemberRoomRepository memberRoomRepository;
	private final RoomService roomService;

	// 회원방을 DTO로 저장
	@Transactional
	public void saveMemberToRoom(Member member, MemberToRoomSaveRequest request) {
		// 방조회
		Room room = roomService.findRoom(request.getRoomId())
			.orElseThrow(() -> CustomException.from(ROOM_NOT_FOUND));

		// 회원방 저장
		MemberRoom memberRoom = MemberRoom.builder()
			.member(member)
			.room(room)
			.build();
		memberRoomRepository.save(memberRoom);
	}

	// 회원방 저장
	@Transactional
	public void save(MemberRoom memberRoom) {
		memberRoomRepository.save(memberRoom);
	}

	// 회원방 존재 조회
	public boolean existsByMemberAndRoom(Long memberId, Long roomId) {
		return memberRoomRepository.existsByMember_IdAndRoom_Id(memberId, roomId);
	}

	// 방에 존재하는 회원인지 판별
	public void validateMemberRoom(Long memberId, Long roomId) {
		if (!memberRoomRepository.existsByMember_IdAndRoom_Id(memberId, roomId)) {
			throw CustomException.from(MEMBER_ROOM_NOT_FOUND);
		}
	}
}
