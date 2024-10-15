package middle_point_search.backend.domains.member.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.util.encoder.PasswordEncoderUtil;
import middle_point_search.backend.domains.member.domain.LogoutToken;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.domain.Role;
import middle_point_search.backend.domains.member.repository.LogoutRepository;
import middle_point_search.backend.domains.member.repository.MemberRepository;
import middle_point_search.backend.domains.member.repository.RefreshTokenRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

	private final MemberRepository memberRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final LogoutRepository logoutRepository;
	private final PasswordEncoderUtil passwordEncoderUtil;

	// 회원가입하기
	@Transactional
	public Member createMember(String name, String pw) {
		pw = passwordEncoderUtil.encodePassword(pw);

		Member member = Member.from(name, pw, Role.USER);
		memberRepository.save(member);

		return member;
	}

	// 회원 로그아웃 하기
	@Transactional
	public void logoutMember(Member member, String accessToken) {
		Long memberId = member.getId();

		// 회원의 refreshToken 삭제
		refreshTokenRepository.deleteByMemberId(memberId);

		// 같은 accessToken으로 다시 로그인하지 못하도록 블랙리스트에 저장
		logoutRepository.save(new LogoutToken(accessToken));
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
