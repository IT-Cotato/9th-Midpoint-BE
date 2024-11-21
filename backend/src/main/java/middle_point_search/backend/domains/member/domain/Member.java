package middle_point_search.backend.domains.member.domain;

import org.apache.commons.lang3.RandomStringUtils;

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
	private String email;

	@Column(nullable = false)
	private String pw;

	@Column(nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	@Column(nullable = true)
	private String provider;

	@Column(nullable = true)
	private String providerId;

	private Member(
		String email,
		String pw,
		String name,
		Role role,
		String provider,
		String providerId
	) {
		this.email = email;
		this.pw = pw;
		this.name = name;
		this.role = role;
		this.provider = provider;
		this.providerId = providerId;
	}

	public static Member createStandardMember(
		String email,
		String pw,
		String name,
		Role role
	) {
		return new Member(
			email,
			pw,
			name,
			role,
			null,
			null
		);
	}

	public static Member createOAuthMember(
		String email,
		String name,
		Role role,
		String provider,
		String providerId
	) {
		return new Member(
			email,
			RandomStringUtils.randomAlphanumeric(20),
			name,
			role,
			provider,
			providerId
		);
	}
}
