package middle_point_search.backend.common.scheduling.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.domains.market.service.MarketService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SchedulingService {

	private final MarketService marketService;

	@Transactional(readOnly = false)
	@Async
	@Scheduled(cron = "0 0 3 * * MON") //월요일 오전 세시에 초기화
	public void autoUpdate() {
		marketService.updateMarket();
	}
}
