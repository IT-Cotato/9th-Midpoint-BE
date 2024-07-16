package middle_point_search.backend.common.scheduling.service;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.domains.market.service.MarketService;
import middle_point_search.backend.domains.room.repository.RoomRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SchedulingService {

	private final RoomRepository roomRepository;
	private final MarketService marketService;

	@Transactional(readOnly = false)
	@Async
	@Scheduled(cron = "0 0 2 * * *") //오전 두시에 매번 초기화
	public void autoDelete() {
		roomRepository.deleteByCreatedAtBefore(LocalDateTime.now().minusDays(3));
	}

	@Transactional(readOnly = false)
	@Async
	@Scheduled(cron = "0 0 3 * * MON") //오전 두시에 매번 초기화
	public void autoUpdate() {
		marketService.updateMarket();
	}
}
