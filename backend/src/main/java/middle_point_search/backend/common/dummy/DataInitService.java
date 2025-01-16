package middle_point_search.backend.common.dummy;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.dummy.repository.JDBCRepository;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.domain.Role;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DataInitService {

	private final PasswordEncoder passwordEncoder;
	private final JDBCRepository JDBCRepository;

	@Transactional
	public void initializeData() {
		log.info("DataInitService.initializeData ======================");

		// 시간 재기 추후 삭제
		long startTime = System.currentTimeMillis();

		String password = passwordEncoder.encode("1234");

		// Members 생성
		List<Member> members = new ArrayList<>();
		for (int i = 1; i < 500000; i++) {
			Member member = Member.createWithoutAddress(
				"user" + i + "@test.com",
				password,
				"user" + i,
				Role.USER);
			members.add(member);
		}
		JDBCRepository.saveAll(members);

		// 시간 재기, 추후 삭제
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		log.info("Data initialization took " + duration + " milliseconds");
	}
}
