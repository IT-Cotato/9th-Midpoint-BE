package middle_point_search.backend.domains.member.domain;

import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;

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
import middle_point_search.backend.domains.member.dto.OAuth2UserInfo;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

	@Id
	@Column(name = "member_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 254)
	private String email;

	@Column(nullable = false)
	private String pw;

	@Column(nullable = false, length = 30)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	@Column(nullable = false)
	@ColumnDefault("false")
	private Boolean existAddress;

	@Column(nullable = true)
	private String profileImagePath;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AuthType authType;

	@Column(nullable = true)
	private String provider;

	@Column(nullable = true)
	private String providerId;

	//주소
	private String siDo;
	private String siGunGu;
	private String roadNameAddress;
	private Double addressLatitude;
	private Double addressLongitude;

	private Member(
		String email,
		String pw,
		String name,
		Role role,
		Boolean existAddress,
		String siDo,
		String siGunGu,
		String roadNameAddress,
		Double addressLatitude,
		Double addressLongitude,
		AuthType authType,
		String provider,
		String providerId
	) {
		this.email = email;
		this.pw = pw;
		this.name = name;
		this.role = role;
		this.existAddress = existAddress;
		this.siDo = siDo;
		this.siGunGu = siGunGu;
		this.roadNameAddress = roadNameAddress;
		this.addressLatitude = addressLatitude;
		this.addressLongitude = addressLongitude;
		this.authType = authType;
		this.provider = provider;
		this.providerId = providerId;
	}

	// 주소 없이 회원 생성
	public static Member createWithoutAddress(String email, String pw, String name, Role role) {

		return new Member(
			email,
			pw,
			name,
			role,
			false,
			null,
			null,
			null,
			null,
			null,
			AuthType.LOCAL,
			null,
			null
		);
	}

	// 주소와 함께 회원 생성
	public static Member createWithAddress(
		String email,
		String pw,
		String name,
		Role role,
		String siDo,
		String siGunGu,
		String roadNameAddress,
		Double addressLatitude,
		Double addressLongitude
	) {
		return new Member(
			email,
			pw,
			name,
			role,
			true,
			siDo,
			siGunGu,
			roadNameAddress,
			addressLatitude,
			addressLongitude,
			AuthType.LOCAL,
			null,
			null
		);
	}

	// OAuth 회원 생성
	public static Member createOAuthMember(OAuth2UserInfo oAuth2UserInfo) {
		return new Member(
			oAuth2UserInfo.getEmail(),
			UUID.randomUUID().toString(),
			oAuth2UserInfo.getName(),
			Role.USER,
			false,
			null,
			null,
			null,
			null,
			null,
			AuthType.OAUTH,
			oAuth2UserInfo.getProvider(),
			oAuth2UserInfo.getProviderId()
		);
	}

	// 비밀번호 변경
	public void updatePassword(String encodedPassword) {
		this.pw = encodedPassword;

	}

	// 이름 변경
	public void updateName(String name) {
		this.name = name;
	}

	// 주소 변경
	public void updateAddress(
		String siDo,
		String siGunGu,
		String roadNameAddress,
		Double addressLatitude,
		Double addressLongitude
	) {
		this.existAddress = true;
		this.siDo = siDo;
		this.siGunGu = siGunGu;
		this.roadNameAddress = roadNameAddress;
		this.addressLatitude = addressLatitude;
		this.addressLongitude = addressLongitude;
	}

	// 주소 삭제
	public void deleteAddress() {
		this.existAddress = false;
		this.siDo = null;
		this.siGunGu = null;
		this.roadNameAddress = null;
		this.addressLatitude = null;
		this.addressLongitude = null;
	}

	// 프로필 이미지 path 변경
	public void updateProfileImagePath(String profileImagePath) {
		this.profileImagePath = profileImagePath;
	}
}
