package middle_point_search.backend.common.dummy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.domains.member.repository.MemberRepository;

@Slf4j
@Profile("!main")
@Configuration
@RequiredArgsConstructor
public class DataInitConfig {

	@Bean
	CommandLineRunner initData(MemberRepository memberRepo, DataInitService dataInitService) {
		return args -> {
			if (memberRepo.count() == 0) {
				// 시간 재기 추후 삭제
				long startTime = System.currentTimeMillis();

				dataInitService.initializeMemberData();
				dataInitService.initializeRoomData();
				dataInitService.initializeMemberRoomData();
				dataInitService.initializePlaceData();
				dataInitService.initializePlaceVoteRoomData();

				// 시간 재기, 추후 삭제
				long endTime = System.currentTimeMillis();
				long duration = endTime - startTime;
				log.info("Data initialization took " + duration + " milliseconds");
			}

		};
	}
}
