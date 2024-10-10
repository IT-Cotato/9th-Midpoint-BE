package middle_point_search.backend.domains.market.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import middle_point_search.backend.domains.market.domain.Market;

public interface MarketRepository extends JpaRepository<Market, Long> {

	@Modifying
	@Query(value = "DELETE FROM Market")
	void deleteAllMarket();
}
