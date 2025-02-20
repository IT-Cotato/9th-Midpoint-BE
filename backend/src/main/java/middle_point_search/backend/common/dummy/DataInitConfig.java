package middle_point_search.backend.common.dummy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.domains.member.repository.MemberRepository;

@Profile("!main")
@Configuration
@RequiredArgsConstructor
public class DataInitConfig {

	@Bean
	CommandLineRunner initData(MemberRepository memberRepo, DataInitService dataInitService) {
		return args -> {
			if (memberRepo.count() == 0) {
				dataInitService.initializeData();
			}
		};
	}
}
