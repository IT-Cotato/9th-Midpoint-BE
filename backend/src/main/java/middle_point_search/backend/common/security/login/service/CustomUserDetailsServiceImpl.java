package middle_point_search.backend.common.security.login.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.exception.RoomNotFoundException;
import middle_point_search.backend.common.security.dto.CustomUserDetailsImpl;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.repository.MemberRepository;
import middle_point_search.backend.domains.member.service.MemberService;
import middle_point_search.backend.domains.room.repository.RoomRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

	private final MemberRepository memberRepository;
	private final RoomRepository roomRepository;
	private final MemberService memberService;

	@Override
	@Transactional
	public UserDetails loadUserByUsernameAndRoomId(String roomId, String name, String pw) throws
		UsernameNotFoundException,
		RoomNotFoundException {

		roomRepository.findRoomByIdentityNumber(roomId);
		if (!roomRepository.existsByIdentityNumber(roomId)) {
			throw new RoomNotFoundException("해당하는 방이 존재하지 않습니다");
		}

		Optional<Member> optionalMember = memberRepository
			.findByRoom_IdentityNumberAndName(roomId, name);

		//멤버가 존재하지 않으면 생성
		Member member = optionalMember.orElseGet(() -> memberService.createMember(roomId, name, pw));

		return CustomUserDetailsImpl.of(roomId, name, member.getPw(), "USER", true);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return null;
	}

}
