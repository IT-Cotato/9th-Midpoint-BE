package middle_point_search.backend.domains.memberRoom;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberRoomValidateService {

	private final MemberRoomRepository memberRoomRepository;

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
