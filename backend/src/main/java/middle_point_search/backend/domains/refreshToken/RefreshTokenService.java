package middle_point_search.backend.domains.refreshToken;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Transactional(readOnly = true)
public class RefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;

	// MemberId로 RefreshToken 조회
	public Optional<RefreshToken> findByMemberId(Long memberId) {
		return refreshTokenRepository.findByMemberId(memberId);
	}

	// RefreshToken 저장
	@Transactional
	public void save(RefreshToken refreshToken) {
		refreshTokenRepository.save(refreshToken);
	}

	// RefreshToken 값을 통해 조회
	public Optional<RefreshToken> findByRefreshToken(String refreshToken) {
		return refreshTokenRepository.findById(refreshToken);
	}

	// MemberId로 RefreshToken 삭제
	@Transactional
	public void deleteByMemberId(Long memberId) {
		refreshTokenRepository.deleteByMemberId(memberId);
	}
}
