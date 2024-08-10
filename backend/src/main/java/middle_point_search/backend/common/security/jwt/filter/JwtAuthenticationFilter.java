package middle_point_search.backend.common.security.jwt.filter;

import static middle_point_search.backend.common.exception.errorCode.CommonErrorCode.UNAUTHORIZED;
import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.common.properties.SecurityProperties;
import middle_point_search.backend.common.security.jwt.provider.JwtTokenProvider;
import middle_point_search.backend.domains.member.repository.MemberRepository;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.domain.RoomType;
import middle_point_search.backend.domains.room.repository.RoomRepository;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final MemberRepository memberRepository;
	private final SecurityProperties securityProperties;
	private final AntPathMatcher pathMatcher;
	private final RoomRepository roomRepository;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String path = request.getRequestURI();

		boolean result = Arrays.stream(securityProperties.getPermitUrls())
			.anyMatch(permitUrl -> pathMatcher.match(permitUrl, path));

		log.info("JwtAuthenticationFilter.shouldNotFilter({}) : {}", path, result);

		return result;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException, CustomException {

		final String refreshToken = jwtTokenProvider.extractRefreshToken(request).orElse(null);
		final String accessToken = jwtTokenProvider.extractAccessToken(request).orElse(null);
		final String tokenRoomId = jwtTokenProvider.extractRoomId(accessToken).orElse(null);

		//1. access토큰이 존재하며, accessToken이 유효하면 인증
		//2. access토큰이 존재하며, accesToken이 유효하지 않으면 에러 리턴
		//3. refresh토큰이 존재하며, refreshToken이 유효하면 access 토큰 재발급
		//4. refresh토큰이 존재하며, refreshToken이 유효하지 않으면 로그인 유도
		//5. 그 외 모든 경우는 에러 리턴
		if (accessToken != null && jwtTokenProvider.isTokenValid(accessToken)) {

			//토큰이 logout된 토큰인지 검사
			if (jwtTokenProvider.isLogout(accessToken)) {
				log.info("logout된 accessToken으로 인증 실패");
				throw new CustomException(INVALID_ACCESS_TOKEN);
			}

			//토큰의 있는 방의 Type과 헤더와 RoomType이 같은지 검사(room검사보다 먼저 되어야 함)
			final RoomType nowRoomType = jwtTokenProvider.extractRoomType(request).orElse(null);
			final Room room = roomRepository.findByIdentityNumber(tokenRoomId)
				.orElseThrow(() -> new CustomException(ROOM_NOT_FOUND));
			if (nowRoomType != room.getRoomType()) {
				throw new CustomException(ROOM_TYPE_UNPROCESSABLE);
			}

			//토큰이 다른 room의 토큰인지 검사
			final String nowRoomId = jwtTokenProvider.extractRoomId(request).orElse(null);
			if (!Objects.equals(nowRoomId, tokenRoomId)) {
				log.info("다른 방의 accessToken으로 인증 실패");
				throw new CustomException(UNAUTHORIZED);
			}

			log.info("access토큰 인증 성공");
			Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
			saveAuthentication(authentication);
			filterChain.doFilter(request, response);
		} else if (accessToken != null && !jwtTokenProvider.isTokenValid(accessToken)) {
			log.info("access토큰 인증 실패");
			throw new CustomException(INVALID_ACCESS_TOKEN);
		} else if (refreshToken != null && jwtTokenProvider.isTokenValid(refreshToken)) {
			log.info("refresh토큰 인증 성공");
			jwtTokenProvider.checkRefreshTokenAndReIssueAccessAndRefreshToken(response, refreshToken);
		} else if (refreshToken != null && !jwtTokenProvider.isTokenValid(refreshToken)) {
			log.info("refresh토큰 인증 실패");
			throw new CustomException(INVALID_REFRESH_TOKEN);
		} else {
			log.info("인증 실패");
			throw new CustomException(UNAUTHORIZED);
		}
	}

	private void saveAuthentication(Authentication authentication) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}
}
