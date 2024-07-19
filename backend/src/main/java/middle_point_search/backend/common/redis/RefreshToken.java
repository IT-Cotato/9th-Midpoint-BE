package middle_point_search.backend.common.redis;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "refreshToken", timeToLive = 86400 )
public class RefreshToken {

	@Id
	private String refreshToken;

	private Long memberId;

	public RefreshToken(final String refreshToken, final Long memberId) {
		this.refreshToken = refreshToken;
		this.memberId = memberId;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public Long getMemberId() {
		return memberId;
	}
}
