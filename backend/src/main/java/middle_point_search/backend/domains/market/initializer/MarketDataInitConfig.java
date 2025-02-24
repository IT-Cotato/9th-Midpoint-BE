package middle_point_search.backend.domains.market.initializer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.domains.market.repository.MarketRepository;
import middle_point_search.backend.domains.market.service.MarketService;

@Slf4j
@Configuration
public class MarketDataInitConfig {

	@Bean
	CommandLineRunner initMarketData(MarketRepository marketRepository, MarketService marketService) {
		return args -> {
			if (marketRepository.count() == 0) {
				marketService.updateMarket();
			}
		};
	}
}