package middle_point_search.backend.common.dummy;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.domain.Role;
import middle_point_search.backend.domains.member.repository.MemberRepository;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DataInitService {

	private final PasswordEncoder passwordEncoder;
	private final MemberRepository memberRepo;


	@Transactional
	public void initializeData() {
		log.info("DataInitService.initializeData ======================");

		// Members 생성
		List<Member> members = new ArrayList<>();
		for (int i = 1; i < 500000; i++) {
			Member member = Member.createWithoutAddress(
				"user" + i + "@test.com",
				passwordEncoder.encode("1234"),
				"user" + i,
				Role.USER);
			members.add(member);
		}
		memberRepo.saveAll(members);
	}
}
