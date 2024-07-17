package middle_point_search.backend.domains.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import middle_point_search.backend.domains.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByRoom_IdentityNumberAndName(String identityNumber, String name);

	Optional<Member> findByRefreshToken(String refreshToken);

	List<Member> findAllByRoom_IdentityNumber(String roomId);
}
