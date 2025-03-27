package middle_point_search.backend.domains.refreshToken.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.domains.refreshToken.repository.RefreshTokenRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;

	// MemberId로 RefreshToken 삭제
	@Transactional
	public void deleteByMemberId(Long memberId) {
		refreshTokenRepository.findByMemberId(memberId)
			.ifPresent(refreshTokenRepository::delete);
	}
}
