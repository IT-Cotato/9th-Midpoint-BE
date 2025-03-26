package middle_point_search.backend.domains.refreshToken.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import middle_point_search.backend.domains.refreshToken.domain.RefreshToken;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

	Optional<RefreshToken> findByMemberId(Long memberId);
}
