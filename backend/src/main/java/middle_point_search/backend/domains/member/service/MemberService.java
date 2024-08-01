package middle_point_search.backend.domains.member.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.common.exception.errorCode.UserErrorCode;
import middle_point_search.backend.common.redis.LogoutRepository;
import middle_point_search.backend.common.redis.LogoutToken;
import middle_point_search.backend.common.redis.RefreshToken;
import middle_point_search.backend.common.redis.RefreshTokenRepository;
import middle_point_search.backend.common.security.exception.RoomNotFoundException;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.domain.Role;
import middle_point_search.backend.domains.member.repository.MemberRepository;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.domain.RoomType;
import middle_point_search.backend.domains.room.repository.RoomRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

	private final RoomRepository roomRepository;
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final RefreshTokenRepository refreshTokenRepository;
	private final LogoutRepository logoutRepository;

	// 회원가입하기
	@Transactional
	public Member createMember(String roomId, String name, String pw) throws RoomNotFoundException {

		pw = encodePassword(pw);
		Room room = roomRepository.findRoomByIdentityNumber(roomId)
			.orElseThrow(() -> new RoomNotFoundException("해당하는 방이 존재하지 않습니다"));

		Role role = Role.USER;

		Member member = Member.from(room, name, pw, role);
		memberRepository.save(member);

		return member;
	}

	// 회원 로그아웃 하기
	@Transactional
	public void logoutMember(String roomId, String name, String accessToken) {
		Member member = memberRepository.findByRoom_IdentityNumberAndName(roomId, name)
			.orElseThrow(() -> new CustomException(UserErrorCode.MEMBER_NOT_FOUND));
		Long memberId= member.getId();

		// 회원의 refreshToken 삭제
		RefreshToken refreshToken = refreshTokenRepository.findByMemberId(memberId).orElse(null);
		refreshTokenRepository.deleteById(refreshToken.getRefreshToken());

		// 같은 accessToken으로 다시 로그인하지 못하도록 블랙리스트에 저장
		logoutRepository.save(new LogoutToken(UUID.randomUUID().toString(), accessToken));
	}

	// 패스워드 인코딩
	private String encodePassword(String rawPw) {
		return passwordEncoder.encode(rawPw);
	}

	// 요청된 pw와 Member의 pw가 일치하는 지 확인
	public boolean matchPassword(String rawPw, String pw) {
		return passwordEncoder.matches(rawPw, pw);
	}
}
