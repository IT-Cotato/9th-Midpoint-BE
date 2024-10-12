package middle_point_search.backend.common.util;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.repository.MemberRepository;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberLoader {

	private final MemberRepository memberRepository;


	// Authentication 객체에서 Member를 찾는 메서드
	public Member getMember() {
		String name = getName();

		return memberRepository.findByName(name)
			.orElseThrow(() -> CustomException.from(MEMBER_NOT_FOUND));
	}

	// Authentication 객체에서 email을 추출하는 메서드
	public String getName() {
		return (String)SecurityContextHolder
			.getContext()
			.getAuthentication()
			.getPrincipal();
	}
}
