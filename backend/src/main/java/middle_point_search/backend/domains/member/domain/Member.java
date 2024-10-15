package middle_point_search.backend.domains.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import middle_point_search.backend.common.baseEntity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

	@Id
	@Column(name = "member_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String name;

	@Column(nullable = false)
	private String pw;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	private Member(String name, String pw, Role role) {
		this.name = name;
		this.pw = pw;
		this.role = role;
	}

	public static Member from(String name, String pw, Role role) {


		return new Member(name, pw, role);
	}

	public void updateRole(Role role) {
		this.role = role;
	}
}
