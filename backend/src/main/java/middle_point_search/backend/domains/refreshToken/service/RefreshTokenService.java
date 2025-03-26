package middle_point_search.backend.domains.refreshToken.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.domains.refreshToken.domain.RefreshToken;
import middle_point_search.backend.domains.refreshToken.repository.RefreshTokenRepository;

@Service
@RequiredArgsConstructor
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
		refreshTokenRepository.findByMemberId(memberId)
			.ifPresent(refreshTokenRepository::delete);
	}
}
