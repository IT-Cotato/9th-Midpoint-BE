package middle_point_search.backend.domains.memberRoom;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberRoomService {

	private final MemberRoomRepository memberRoomRepository;

	public Boolean existsByMemberIdAndRoomId(Long memberId, Long roomId) {
		return memberRoomRepository.existsByMember_IdAndRoom_Id(memberId, roomId);
	}
}
