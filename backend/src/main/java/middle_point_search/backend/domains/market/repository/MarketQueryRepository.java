package middle_point_search.backend.domains.market.repository;

import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import middle_point_search.backend.domains.market.domain.Market;

@Repository
@RequiredArgsConstructor
public class MarketQueryRepository {

	private final JdbcTemplate jdbcTemplate;

	@Transactional
	public void saveAll(List<Market> markets) {
		String sql = "INSERT INTO market (name, si_gun_gu, si_do, address_latitude, address_longitude) " +
			"VALUES (?, ?, ?, ?, ?)";

		jdbcTemplate.batchUpdate(sql,
			markets,
			markets.size(),
			(PreparedStatement ps, Market market) -> {
				ps.setString(1, market.getName());
				ps.setString(2, market.getSiGunGu());
				ps.setString(3, market.getSiDo());
				ps.setDouble(4, market.getAddressLatitude());
				ps.setDouble(5, market.getAddressLongitude());
			});
	}
}
