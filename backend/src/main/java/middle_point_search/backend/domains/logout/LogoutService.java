package middle_point_search.backend.domains.logout;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Transactional(readOnly = true)
public class LogoutService {

	private final LogoutRepository logoutRepository;

	// LogoutToken 저장
	@Transactional
	public void save(LogoutToken logoutToken) {
		logoutRepository.save(logoutToken);
	}

	// AccessToken으로 LogoutToken 존재 여부 확인
	public boolean existsByAccessToken(String accessToken) {
		return logoutRepository.existsByAccessToken(accessToken);
	}
}
