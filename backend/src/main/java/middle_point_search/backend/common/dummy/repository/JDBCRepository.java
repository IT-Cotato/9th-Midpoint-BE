package middle_point_search.backend.common.dummy.repository;

import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import middle_point_search.backend.domains.member.domain.Member;

@Repository
@RequiredArgsConstructor
public class JDBCRepository {

	private final JdbcTemplate jdbcTemplate;

	@Transactional
	public void saveAll(List<Member> members) {
		String sql = "INSERT INTO member (email, pw, name, role, exist_address) " +
			"VALUES (?, ?, ?, ?, ?)";

		jdbcTemplate.batchUpdate(sql,
			members,
			members.size(),
			(PreparedStatement ps, Member member) -> {
				ps.setString(1, member.getEmail());
				ps.setString(2, member.getPw());
				ps.setString(3, member.getName());
				ps.setString(4, member.getRole().name());
				ps.setBoolean(5, member.getExistAddress());
			});
	}
}
