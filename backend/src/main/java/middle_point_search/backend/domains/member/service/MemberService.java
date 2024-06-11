package middle_point_search.backend.domains.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.RoomNotFoundException;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.repository.MemberRepository;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.repository.RoomRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

	private final RoomRepository roomRepository;
	private final MemberRepository memberRepository;

	@Transactional(readOnly = false)
	public Member createMember(String roomId, String name, String pw) throws RoomNotFoundException {
		Room room = roomRepository.findRoomByIdentityNumber(roomId)
			.orElseThrow(() -> new RoomNotFoundException("해당하는 방이 존재하지 않습니다"));

		Member member = Member.from(room, name, pw);
		memberRepository.save(member);

		return member;
	}
}
