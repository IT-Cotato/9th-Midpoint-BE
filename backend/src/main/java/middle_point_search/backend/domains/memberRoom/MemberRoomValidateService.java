package middle_point_search.backend.domains.memberRoom;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.room.domain.Room;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberRoomValidateService {

	private final MemberRoomRepository memberRoomRepository;

	// 중복된 회원방이 있는지 확인
	public void validateDuplicatedMemberRoom(Member member, Room room) {
		Boolean existence = memberRoomRepository.existsByMember_IdAndRoom_Id(member.getId(), room.getId());

		if (existence) {
			throw CustomException.from(DUPLICATE_MEMBER_ROOM);
		}
	}

	// 방에 존재하는 회원인지 판별
	public void validateAuthorizedMember(Long memberId, Long roomId) {
		if (!memberRoomRepository.existsByMember_IdAndRoom_Id(memberId, roomId)) {
			throw CustomException.from(UNAUTHORIZED_MEMBER_ROOM);
		}
	}
}
