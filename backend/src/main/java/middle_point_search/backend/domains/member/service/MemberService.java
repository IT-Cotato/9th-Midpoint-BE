package middle_point_search.backend.domains.member.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.security.exception.RoomNotFoundException;
import middle_point_search.backend.common.util.MemberLoader;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.domain.Role;
import middle_point_search.backend.domains.member.repository.MemberRepository;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.repository.RoomRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

	private final RoomRepository roomRepository;
	private final MemberRepository memberRepository;
	private final MemberLoader memberLoader;
	private final PasswordEncoder passwordEncoder;

	@Transactional(readOnly = false)
	public Member createMember(String roomId, String name, String pw) throws RoomNotFoundException {

		Role role = Role.GUEST; // 회원 가입시 권한은 GUEST
		pw = encodePassword(pw);

		Room room = roomRepository.findRoomByIdentityNumber(roomId)
			.orElseThrow(() -> new RoomNotFoundException("해당하는 방이 존재하지 않습니다"));

		Member member = Member.from(room, name, pw, role);
		memberRepository.save(member);

		return member;
	}

	@Transactional(readOnly = false)
	public void logoutMember() {
		Member member = memberLoader.getMember();

		member.destroyRefreshToken();
	}

	// 패스워드 인코딩
	private String encodePassword(String rawPw) {
		return passwordEncoder.encode(rawPw);
	}

	// 요청된 pw와 Member의 pw가 일치하는 지 확인
	public boolean matchPassword(String rawPw, String pw) {
		return passwordEncoder.matches(rawPw, pw);
	}

	@Transactional(readOnly = false)
	public void updateMemberRole(Member member, Role role) {
		member.updateRole(role);
	}

	@Transactional(readOnly = false)
	public void updateRomeMembersRole(String roomId, Role role) {
		List<Member> members = memberRepository.findAllByRoom_IdentityNumber(roomId);

		members.forEach(member -> member.updateRole(role));
	}
}
