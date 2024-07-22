package middle_point_search.backend.common.redis;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@RedisHash(value= "blacklist", timeToLive = 60*60)
public class LogoutToken {

	@Id
	private String id;

	@Indexed
	private String accessToken;
}
