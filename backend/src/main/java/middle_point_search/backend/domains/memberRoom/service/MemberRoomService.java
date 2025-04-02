package middle_point_search.backend.domains.memberRoom.service;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.repository.MemberRepository;
import middle_point_search.backend.domains.memberRoom.domain.MemberRoom;
import middle_point_search.backend.domains.memberRoom.dto.MemberRoomDTO.ExistsMemberRoomResponse;
import middle_point_search.backend.domains.memberRoom.dto.MemberRoomDTO.FindRoomsByMemberIdResponse;
import middle_point_search.backend.domains.memberRoom.repository.MemberRoomRepository;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.repository.RoomRepository;
import middle_point_search.backend.domains.room.service.RoomValidationService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberRoomService {

	private final MemberRoomRepository memberRoomRepository;
	private final MemberRoomValidateService memberRoomValidateService;
	private final RoomRepository roomRepository;
	private final RoomValidationService roomValidationService;
	private final MemberRepository memberRepository;

	// 회원방을 DTO로 저장
	@Transactional(rollbackFor = CustomException.class)
	public void saveMemberToRoom(Long memberId, String roomId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> CustomException.from(MEMBER_NOT_FOUND));
		Room room = roomRepository.findById(roomId)
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
	public List<FindRoomsByMemberIdResponse> findRooms(Long memberId) {
		List<MemberRoom> memberRooms = memberRoomRepository.findByMember_Id(memberId);

		return memberRooms.stream()
			.map(memberRoom -> FindRoomsByMemberIdResponse.from(memberRoom.getRoom()))
			.toList();
	}

	// 회원방이 존재하는지 확인
	public ExistsMemberRoomResponse existsMemberRoom(Long memberId, String roomId) {
		Boolean exists = memberRoomRepository.existsByMember_IdAndRoom_Id(memberId, roomId);

		return ExistsMemberRoomResponse.from(exists);
	}

	// 회원방에서 회원 삭제, 방이 없으면 방 삭제
	@Transactional
	public void deleteMemberFromRoom(Long memberId, String roomId) {
		roomValidationService.validateRoomExisting(roomId);
		memberRoomValidateService.validateAuthorizedMember(memberId, roomId);

		memberRoomRepository.deleteByRoomIdAndMemberId(roomId, memberId);

		// 방에 멤버가 없으면 방 삭제
		if (!memberRoomRepository.existsByRoomId(roomId)) {
			roomRepository.deleteById(roomId);
		}
	}
}
