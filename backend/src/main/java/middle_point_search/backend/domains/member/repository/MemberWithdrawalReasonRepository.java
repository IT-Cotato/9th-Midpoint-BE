package middle_point_search.backend.domains.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import middle_point_search.backend.domains.member.domain.MemberWithdrawalReason;

public interface MemberWithdrawalReasonRepository extends JpaRepository<MemberWithdrawalReason, Long> {
}
