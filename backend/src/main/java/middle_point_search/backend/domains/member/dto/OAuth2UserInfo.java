package middle_point_search.backend.domains.member.dto;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class OAuth2UserInfo {
	private final static String GOOGLE = "google";
	private final static String KAKAO = "kakao";
	private final static String NAVER = "naver";

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
		// 일반 유저와 구분하기 위해 뒤에 (google) 추가
		String email = String.format("%s(%s)", (String)attributes.get("email"), GOOGLE);

		return OAuth2UserInfo.builder()
			.provider(GOOGLE)
			.name((String)attributes.get("name"))
			.email(email)
			.providerId((String)attributes.get("sub"))
			.build();
	}

	private static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
		// 일반 유저와 구분하기 위해 뒤에 (kakao) 추가
		String email = String.format("%s(%s)", (String)((Map)attributes.get("kakao_account")).get("email"), KAKAO);

		return OAuth2UserInfo.builder()
			.provider(KAKAO)
			.name((String)((Map)attributes.get("properties")).get("nickname"))
			.email(email)
			.providerId(attributes.get("id").toString())
			.build();
	}

	private static OAuth2UserInfo ofNaver(Map<String, Object> attributes) {
		// 일반 유저와 구분하기 위해 뒤에 (naver) 추가
		String email = String.format("%s(%s)", (String)((Map)attributes.get("response")).get("email"), NAVER);

		return OAuth2UserInfo.builder()
			.provider(NAVER)
			.name((String)((Map)attributes.get("response")).get("name"))
			.email(email)
			.providerId((String)((Map)attributes.get("response")).get("id"))
			.build();
	}
}