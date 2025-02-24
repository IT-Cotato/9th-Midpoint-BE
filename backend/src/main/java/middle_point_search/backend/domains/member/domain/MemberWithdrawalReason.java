package middle_point_search.backend.domains.member.domain;

import static jakarta.persistence.GenerationType.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class MemberWithdrawalReason {

	@Id
	@Column(name = "member_withdrawal_reason_id")
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	private String reason;

	public MemberWithdrawalReason(String reason) {
		this.reason = reason;
	}
}
