package middle_point_search.backend.common.security.jwt.provider;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;
import java.security.Key;
import java.util.Date;
import java.util.Optional;
import middle_point_search.backend.common.properties.JwtProperties;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
import middle_point_search.backend.common.security.dto.CustomUserDetails;
import middle_point_search.backend.common.security.dto.CustomUserDetailsImpl;
import middle_point_search.backend.common.security.jwt.dto.JwtDTO.AccessAndRefreshTokenResponse;
import middle_point_search.backend.common.security.jwt.dto.JwtDTO.AccessTokenResponse;
import middle_point_search.backend.common.util.ResponseWriter;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.repository.MemberRepository;

@Transactional(readOnly = true)
@Service
@Slf4j
public class JwtTokenProviderImpl implements JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private final MemberRepository memberRepository;
    private final Key key;

    public JwtTokenProviderImpl(JwtProperties jwtProperties, MemberRepository memberRepository) {
        this.jwtProperties = jwtProperties;
        this.memberRepository = memberRepository;
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }


    @Override
    public Authentication getAuthentication(String accessToken) {
        String roomId = extractRoomId(accessToken).orElseThrow(() -> new CustomException(INVALID_ACCESS_TOKEN));
        String name = extractName(accessToken).orElseThrow(() -> new CustomException(INVALID_ACCESS_TOKEN));

        Member member = memberRepository.findByRoom_IdentityNumberAndName(roomId, name)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        CustomUserDetails userDetails = CustomUserDetailsImpl.from(member);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }


    @Override
    public String createAccessToken(String roomId, String name) {
        return Jwts.builder()
                .setSubject(jwtProperties.getACCESS_TOKEN_SUBJECT())
                .claim(jwtProperties.getNAME_CLAIM(), name)
                .claim(jwtProperties.getROOM_ID_CLAIM(), roomId)
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
    public void updateRefreshToken(String roomId, String name, String refreshToken) {
        log.info("updateRefreshToken");
        memberRepository.findByRoom_IdentityNumberAndName(roomId, name)
                .ifPresent(member -> member.updateRefreshToken(refreshToken));
    }

    @Override
    @Transactional
    public void destroyRefreshToken(String roomId, String name) {
        memberRepository.findByRoom_IdentityNumberAndName(roomId, name)
                .ifPresent(Member::destroyRefreshToken);
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
    public Optional<String> extractName(String accessToken) {
        try {
            return Optional.ofNullable(parseClaims(accessToken).get(jwtProperties.getNAME_CLAIM(), String.class));
        } catch (Exception e) {
            log.error("액세스 토큰이 유효하지 않습니다. token: {}", accessToken);
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> extractRoomId(String accessToken) {
        try {
            return Optional.ofNullable(parseClaims(accessToken).get(jwtProperties.getROOM_ID_CLAIM(), String.class));
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

    @Override
    public void checkRefreshTokenAndReIssueAccessAndRefreshToken(HttpServletResponse response, String refreshToken) {
        Member member = memberRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new CustomException(INVALID_REFRESH_TOKEN));
        String roomId = member.getRoom().getIdentityNumber();
        String name = member.getName();

        String accessToken = createAccessToken(roomId, name);
        String newRefreshToken = createRefreshToken();

        updateRefreshToken(roomId, name, newRefreshToken);

        sendAccessAndRefreshToken(response, accessToken, newRefreshToken);
    }
}
