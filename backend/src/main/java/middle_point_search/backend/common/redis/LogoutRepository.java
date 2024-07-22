package middle_point_search.backend.common.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogoutRepository extends CrudRepository<LogoutToken, String> {

	boolean existsByAccessToken(String accessToken);
}
