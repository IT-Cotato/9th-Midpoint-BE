package middle_point_search.backend.common.security.filter.jwtFilter;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.dto.DataResponse;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.common.properties.JwtProperties;
import middle_point_search.backend.common.security.filter.jwtFilter.JwtDTO.AccessAndRefreshTokenResponse;
import middle_point_search.backend.common.security.filter.jwtFilter.JwtDTO.AccessTokenResponse;
import middle_point_search.backend.common.util.ResponseWriter;
import middle_point_search.backend.domains.logout.LogoutService;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.repository.MemberRepository;
import middle_point_search.backend.domains.refreshToken.RefreshToken;
import middle_point_search.backend.domains.refreshToken.RefreshTokenService;

@Transactional(readOnly = true)
@Service
@Slf4j
public class JwtTokenProviderImpl implements JwtTokenProvider {

	private final JwtProperties jwtProperties;
	private final MemberRepository memberRepository;
	private final Key key;
	private final LogoutService logoutService;
	private final RefreshTokenService refreshTokenService;

	public JwtTokenProviderImpl(JwtProperties jwtProperties, MemberRepository memberRepository,
		LogoutService logoutService, RefreshTokenService refreshTokenService) {
		this.jwtProperties = jwtProperties;
		this.memberRepository = memberRepository;
		this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
		this.logoutService = logoutService;
		this.refreshTokenService = refreshTokenService;
	}

	//authentication을 만들어주는 메서드
	@Override
	public Authentication getAuthentication(String accessToken) {
		Long memberId = extractMemberId(accessToken).orElseThrow(() -> CustomException.from(INVALID_ACCESS_TOKEN));

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> CustomException.from(MEMBER_NOT_FOUND));

		String name = member.getName();
		String pw = member.getPw();

		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(member.getRole().getValue()));

		return UsernamePasswordAuthenticationToken.authenticated(name, pw, authorities);
	}

	@Override
	public String createAccessToken(Long memberId) {
		return Jwts.builder()
			.setSubject(jwtProperties.getACCESS_TOKEN_SUBJECT())
			.claim(jwtProperties.getMEMBER_ID_CLAIM(), memberId)
			.setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getAccess().getExpiration() * 1000L))
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	@Override
	public String createRefreshToken() {
		return Jwts.builder()
			.setSubject(jwtProperties.getREFRESH_TOKEN_SUBJECT())
			.setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getRefresh().getExpiration() * 1000L))
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	@Override
	@Transactional
	public void updateRefreshToken(Long memberId, String refreshToken) {
		Optional<RefreshToken> optionalRefreshToken = refreshTokenService.findByMemberId(memberId);

		// refreshToken이 없으면 생성 및 저장, 있으면 refreshToken 값 변경
		RefreshToken refreshTokenObj;
		if (optionalRefreshToken.isEmpty()) {
			refreshTokenObj = new RefreshToken(refreshToken, memberId);
		} else {
			refreshTokenObj = optionalRefreshToken.get();
			refreshTokenObj.setRefreshToken(refreshToken);
		}

		refreshTokenService.save(refreshTokenObj);
	}

	@Override
	@Transactional
	public void destroyRefreshToken(Long memberId) {
		refreshTokenService.deleteByMemberId(memberId);
	}

	@Override
	public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
		setAccessTokenHeader(response, accessToken);
		setRefreshTokenHeader(response, refreshToken);

		AccessAndRefreshTokenResponse accessAndRefreshTokenResponse = AccessAndRefreshTokenResponse.from(accessToken,
			refreshToken);

		ResponseWriter.writeResponse(response, DataResponse.from(accessAndRefreshTokenResponse), HttpStatus.OK);
	}

	@Override
	public void sendAccessToken(HttpServletResponse response, String accessToken) {
		setAccessTokenHeader(response, accessToken);

		AccessTokenResponse accessTokenResponse = AccessTokenResponse.from(accessToken);

		ResponseWriter.writeResponse(response, DataResponse.from(accessTokenResponse), HttpStatus.OK);
	}

	@Override
	public Optional<String> extractAccessToken(HttpServletRequest request) {
		return Optional.ofNullable(request.getHeader(jwtProperties.getAccess().getHeader())).filter(
			accessToken -> accessToken.startsWith(jwtProperties.getBEARER())
		).map(accessToken -> accessToken.replace(jwtProperties.getBEARER(), ""));
	}

	@Override
	public Optional<String> extractRefreshToken(HttpServletRequest request) {
		return Optional.ofNullable(request.getHeader(jwtProperties.getRefresh().getHeader())).filter(
			refreshToken -> refreshToken.startsWith(jwtProperties.getBEARER())
		).map(refreshToken -> refreshToken.replace(jwtProperties.getBEARER(), ""));
	}

	@Override
	public Claims parseClaims(String accessToken) {
		try {
			return Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(accessToken)
				.getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}

	@Override
	public Optional<Long> extractMemberId(String accessToken) {
		try {
			return Optional.ofNullable(parseClaims(accessToken).get(jwtProperties.getMEMBER_ID_CLAIM(), Long.class));
		} catch (Exception e) {
			log.error("액세스 토큰이 유효하지 않습니다. token: {}", accessToken);
			return Optional.empty();
		}
	}

	@Override
	public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
		response.setHeader(jwtProperties.getAccess().getHeader(), accessToken);
	}

	@Override
	public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
		response.setHeader(jwtProperties.getRefresh().getHeader(), refreshToken);
	}

	//access, refresh 토큰의 유효성을 검사하며, 기간, 형식, 변조, 공백 등을 확인
	@Override
	public boolean isTokenValid(String token) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token);
			return true;
		} catch (SecurityException | MalformedJwtException e) {
			log.info("Invalid JWT Token", e);
		} catch (ExpiredJwtException e) {
			log.info("Expired JWT Token", e);
		} catch (UnsupportedJwtException e) {
			log.info("Unsupported JWT Token", e);
		} catch (IllegalArgumentException e) {
			log.info("JWT claims string is empty.", e);
		}
		return false;
	}

	public void checkRefreshTokenAndReIssueAccessAndRefreshToken(HttpServletResponse response, String refreshToken) {
		//refreshToken이 유효한지 확인
		RefreshToken refreshTokenObj = refreshTokenService.findByRefreshToken(refreshToken)
			.orElseThrow(() -> CustomException.from(INVALID_REFRESH_TOKEN));

		Long memberId = refreshTokenObj.getMemberId();

		String newAccessToken = createAccessToken(memberId);
		String newRefreshToken = createRefreshToken();

		//기존 refreshToken 삭제 및 새로운 refreshToken 저장
		updateRefreshToken(memberId, newRefreshToken);

		sendAccessAndRefreshToken(response, newAccessToken, newRefreshToken);
	}

	@Override
	public boolean isLogout(String accessToken) {
		return logoutService.existsByAccessToken(accessToken);
	}
}