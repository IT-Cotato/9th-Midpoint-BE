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
import middle_point_search.backend.domains.place.repository.PlaceRepository;
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
	private final PlaceRepository placeRepository;
	private final PasswordEncoder passwordEncoder;
	private final RefreshTokenRepository refreshTokenRepository;
	private final LogoutRepository logoutRepository;

	// 회원가입하기
	@Transactional
	public Member createMember(String roomId, String name, String pw) throws RoomNotFoundException {

		pw = encodePassword(pw);
		Room room = roomRepository.findByIdentityNumber(roomId)
			.orElseThrow(() -> new RoomNotFoundException("해당하는 방이 존재하지 않습니다"));

		//회원 권한 정하기
		Role role = decideRole(room);

		Member member = Member.from(room, name, pw, role);
		memberRepository.save(member);

		return member;
	}

	// 회원의 ROLE을 Room의 RoomType과 장소 저장 유무를 기준으로 결정하는 메서드
	private Role decideRole(Room room) {
		RoomType roomType = room.getRoomType();
		String identityNumber = room.getIdentityNumber();

		//방 타입이 개인이 모두 저장이고, 이미 장소를 저장했다면 그 이후에 회원가입하는 모든 회원은 권한승인
		if (roomType == RoomType.SELF && placeRepository.existsByRoom_IdentityNumber(identityNumber)) {
			return Role.USER; // SELF인 경우 이미 다 장소가 입력됐으므로 바로 승인
		}
		return Role.GUEST; // 회원 가입시 권한은 GUEST
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

	// 단일 회원 Role 변경하기
	@Transactional
	public void updateMemberRole(Member member, Role role) {
		member.updateRole(role);
	}

	// 여러 회원 Role 변경하기
	@Transactional
	public void updateRomeMembersRole(String roomId, Role role) {
		List<Member> members = memberRepository.findAllByRoom_IdentityNumber(roomId);

		members.forEach(member -> member.updateRole(role));
	}
}
