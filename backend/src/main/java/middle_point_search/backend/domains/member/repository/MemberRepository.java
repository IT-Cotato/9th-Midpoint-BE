package middle_point_search.backend.domains.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import middle_point_search.backend.domains.member.domain.Member;
//import middle_point_search.backend.domains.room.domain.Room;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByRoom_IdentityNumberAndName(String identityNumber, String name);

	Optional<Member> findByRefreshToken(String refreshToken);

}
