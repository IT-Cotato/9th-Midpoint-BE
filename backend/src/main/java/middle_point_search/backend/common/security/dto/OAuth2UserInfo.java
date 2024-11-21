package middle_point_search.backend.common.security.dto;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.domain.Role;

@Builder
@Getter
@ToString
public class OAuth2UserInfo {
	private final static String GOOGLE = "google";
	private final static String KAKAO = "kakao";
	private final static String NAVER = "naver";

	private String loginId;
	private String email;
	private String name;
	private String provider;
	private String providerId;

	public static OAuth2UserInfo of(String provider, Map<String, Object> attributes) {
		switch (provider) {
			case GOOGLE:
				return ofGoogle(attributes);
			case KAKAO:
				return ofKakao(attributes);
			case NAVER:
				return ofNaver(attributes);
			default:
				throw new RuntimeException();
		}
	}

	private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
		return OAuth2UserInfo.builder()
			.provider(GOOGLE)
			.loginId((String)attributes.get("email"))
			.name((String)attributes.get("name"))
			.email((String)attributes.get("email"))
			.providerId((String)attributes.get("sub"))
			.build();
	}

	private static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
		return OAuth2UserInfo.builder()
			.provider(KAKAO)
			.loginId(attributes.get("id").toString())
			.name((String)((Map)attributes.get("properties")).get("nickname"))
			.providerId(attributes.get("id").toString())
			.build();
	}

	private static OAuth2UserInfo ofNaver(Map<String, Object> attributes) {
		return OAuth2UserInfo.builder()
			.provider(NAVER)
			.loginId((String)((Map)attributes.get("response")).get("id"))
			.name((String)((Map)attributes.get("response")).get("name"))
			.providerId((String)((Map)attributes.get("response")).get("id"))
			.build();
	}

	public Member toEntity() {
		return Member.createOAuthMember(loginId, name, Role.USER, provider, providerId);
	}

}