package middle_point_search.backend.domains.market.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import middle_point_search.backend.domains.market.domain.Market;

public interface MarketRepository extends JpaRepository<Market, Long> {
}
