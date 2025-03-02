package middle_point_search.backend.domains.member.service;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.common.exception.errorCode.CommonErrorCode;
import middle_point_search.backend.common.properties.OAuthProperties;
import middle_point_search.backend.domains.member.dto.OAuth2UserInfo;
import middle_point_search.backend.common.security.filter.jwtFilter.JwtTokenProvider;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.dto.response.LoginMemberResponse;
import middle_point_search.backend.domains.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class OAuthService {

	private final OAuthProperties oAuthProperties;
	private final MemberRepository memberRepository;
	private final JwtTokenProvider jwtTokenProvider;

	// OAuth 로그인 및 회원처리
	public LoginMemberResponse login(String provider, String code) {
		String oauthAccessToken = getOauthAccessToken(provider, code);
		OAuth2UserInfo oAuth2UserInfo = getUserInfo(provider, oauthAccessToken);

		// oAuth2UserInfo가 저장되어 있는지 유저 정보 확인
		// 없으면 DB 저장 후 해당 유저를 저장
		// 있으면 해당 유저를 저장
		Member member = memberRepository.findByProviderAndProviderId(
				oAuth2UserInfo.getProvider(),
				oAuth2UserInfo.getProviderId())
			.orElseGet(() -> memberRepository.save(Member.createOAuthMember(oAuth2UserInfo)));

		// AccessToken, RefreshToken 생성
		String accessToken = jwtTokenProvider.createAccessToken(member.getId());
		String refreshToken = jwtTokenProvider.createRefreshToken();

		return new LoginMemberResponse(accessToken, refreshToken);
	}

	public String getOauthAccessToken(String provider, String code) {
		String tokenUrl = findTokenUrl(provider);
		MultiValueMap<String, String> params = findOAuthParams(provider, code);

		WebClient webClient = WebClient.builder().build();
		Map<String, Object> response = webClient.post()
			.uri(tokenUrl)
			.bodyValue(params)
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.retrieve()
			.bodyToMono(Map.class)
			.block();

		return (String) response.get("access_token");
	}

	// AccessToken을 받아오기 위한 파라미터를 찾아주는 메소드
	private MultiValueMap<String, String> findOAuthParams(String provider, String code) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

		System.out.println(oAuthProperties.getGoogle().getClientId());
		System.out.println(oAuthProperties.getGoogle().getClientSecret());
		System.out.println(oAuthProperties.getGoogle().getRedirectUri());

		switch (provider) {
			case "google":
				params.add("client_id", oAuthProperties.getGoogle().getClientId());
				params.add("client_secret", oAuthProperties.getGoogle().getClientSecret());
				params.add("redirect_uri", oAuthProperties.getGoogle().getRedirectUri());
				params.add("grant_type", "authorization_code");
				break;

			case "kakao":
				params.add("client_id", oAuthProperties.getKakao().getClientId());
				params.add("client_secret", oAuthProperties.getKakao().getClientSecret());
				params.add("redirect_uri", oAuthProperties.getKakao().getRedirectUri());
				params.add("grant_type", "authorization_code");
				break;

			case "naver":
				params.add("client_id", oAuthProperties.getNaver().getClientId());
				params.add("client_secret", oAuthProperties.getNaver().getClientSecret());
				params.add("redirect_uri", oAuthProperties.getNaver().getRedirectUri());
				params.add("grant_type", "authorization_code");
				break;

			default:
				throw CustomException.from(CommonErrorCode.BAD_REQUEST);
		}
		params.add("code", code);

		return params;
	}

	// AccessToken을 받아오기 위한 URL을 찾아주는 메소드
	private String findTokenUrl(String provider) {
		String tokenUrl;

		switch (provider) {
			case "google":
				tokenUrl = "https://oauth2.googleapis.com/token";
				break;
			case "kakao":
				tokenUrl = "https://kauth.kakao.com/oauth/token";
				break;
			case "naver":
				tokenUrl = "https://nid.naver.com/oauth2.0/token";
				break;
			default:
				throw CustomException.from(CommonErrorCode.BAD_REQUEST);
		}

		return tokenUrl;
	}

	// AccessToken을 이용하여 사용자 정보를 받아오는 메소드
	public OAuth2UserInfo getUserInfo(String provider, String accessToken) {
		String userInfoUrl = findUserInfoUrl(provider);

		WebClient webClient = WebClient.builder().build();
		Map<String, Object> attributes = webClient.get()
			.uri(userInfoUrl)
			.header("Authorization", "Bearer " + accessToken)
			.retrieve()
			.bodyToMono(Map.class)
			.block();

		return OAuth2UserInfo.of(provider, attributes);
	}

	// 사용자 정보를 받아오기 위한 URL을 찾아주는 메소드
	private String findUserInfoUrl(String provider) {
		String userInfoUrl;

		switch (provider) {
			case "google":
				userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";
				break;
			case "kakao":
				userInfoUrl = "https://kapi.kakao.com/v2/user/me";
				break;
			case "naver":
				userInfoUrl = "https://openapi.naver.com/v1/nid/me";
				break;
			default:
				throw CustomException.from(CommonErrorCode.BAD_REQUEST);
		}

		return userInfoUrl;
	}
}
