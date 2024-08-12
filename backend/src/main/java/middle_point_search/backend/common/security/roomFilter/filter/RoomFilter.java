package middle_point_search.backend.common.security.roomFilter.filter;

import static middle_point_search.backend.common.exception.errorCode.CommonErrorCode.UNAUTHORIZED;
import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

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
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.domain.RoomType;
import middle_point_search.backend.domains.room.repository.RoomRepository;

@Slf4j
@RequiredArgsConstructor
public class RoomFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final SecurityProperties securityProperties;
	private final AntPathMatcher pathMatcher;
	private final RoomRepository roomRepository;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String path = request.getRequestURI();

		boolean result = Arrays.stream(securityProperties.getPermitUrls())
			.anyMatch(permitUrl -> pathMatcher.match(permitUrl, path));

		log.info("RoomFilter.shouldNotFilter({}) : {}", path, result);

		return result;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException, CustomException {

		final String accessToken = jwtTokenProvider.extractAccessToken(request).orElse(null);
		final String headerRoomId = jwtTokenProvider.extractRoomId(request).orElse(null);
		final String tokenRoomId = jwtTokenProvider.extractRoomId(accessToken).orElse(null);
		final RoomType headerRoomType = jwtTokenProvider.extractRoomType(request).orElse(null);

		final Room headerRoom = roomRepository.findByIdentityNumber(headerRoomId)
			.orElseThrow(() -> new CustomException(ROOM_NOT_FOUND));
		final Room room = roomRepository.findByIdentityNumber(tokenRoomId)
			.orElseThrow(() -> new CustomException(ROOM_NOT_FOUND));

		//헤더 ROOM의 있는 방의 Type과 헤더의 RoomType이 같은지 검사(room검사보다 먼저 되어야 함)
		if (headerRoomType != headerRoom.getRoomType()) {
			throw new CustomException(ROOM_TYPE_UNPROCESSABLE);
		}

		//토큰의 있는 방의 Type과 헤더의 RoomType이 같은지 검사(room검사보다 먼저 되어야 함)
		if (headerRoomType != room.getRoomType()) {
			throw new CustomException(ROOM_TYPE_UNPROCESSABLE);
		}

		//토큰이 다른 room의 토큰인지 검사
		if (!Objects.equals(headerRoomId, tokenRoomId)) {
			log.info("다른 방의 accessToken으로 인증 실패");
			throw new CustomException(UNAUTHORIZED);
		}

		log.info("roomId, roomType 인증 성공");
		filterChain.doFilter(request, response);
	}
}
