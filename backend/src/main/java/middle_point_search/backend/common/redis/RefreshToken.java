package middle_point_search.backend.common.redis;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@RedisHash(value = "refreshToken", timeToLive =1209600)
public class RefreshToken {

	@Id
	private String refreshToken;

	@Indexed
	private String accessToken;

	private Long memberId;

	@Transactional
	public void updateAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
}
