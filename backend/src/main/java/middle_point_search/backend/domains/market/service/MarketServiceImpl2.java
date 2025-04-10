package middle_point_search.backend.domains.market.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.domains.market.domain.Market;
import middle_point_search.backend.domains.market.dto.dto.OliveYoungDto;
import middle_point_search.backend.domains.market.dto.dto.StationDto;
import middle_point_search.backend.domains.market.repository.MarketQueryRepository;
import middle_point_search.backend.domains.market.repository.MarketRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MarketServiceImpl2 implements MarketService {

	private final MarketQueryRepository marketQueryRepository;
	private final MarketRepository marketRepository;
	private final OliveYoungService oliveYoungService;
	private final StationService stationService;

	@Override
	@Transactional
	public void updateMarket() {
		List<StationDto> stations = stationService.getAllStations();
		List<OliveYoungDto> oliveYoungs = oliveYoungService.getAllOliveYoung();

		List<Market> markets = new ArrayList<>();
		Set<String> usedOliveYoung = new HashSet<>();

		// 역 근처 올리브영 있으면 Market으로 저장
		stations.forEach(station -> {
			boolean isExist = false;
			for (OliveYoungDto olive : oliveYoungs) {
				if (isNear(station, olive)) {
					isExist = true;
					usedOliveYoung.add(olive.name());
				}
			}
			if (isExist) {
				markets.add(station.toMarketEntity());
			}
		});

		// 한번도 사용되지 않은 올리브영은 Market에 추가
		oliveYoungs.stream()
			.filter(olive -> !usedOliveYoung.contains(olive.name()))
			.forEach(olive -> markets.add(olive.toMarketEntity()));

		marketRepository.deleteAllMarket();
		marketQueryRepository.saveAll(markets);

	}

	private boolean isNear(StationDto station, OliveYoungDto olive) {
		double distance = Math.sqrt(
			Math.pow(station.latitude() - olive.latitude(), 2) + Math.pow(station.longitude() - olive.longitude(), 2)
		);
		return distance < 0.006;
	}
}
