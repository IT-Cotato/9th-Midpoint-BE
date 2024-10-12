package middle_point_search.backend.domains.member.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@RedisHash(value= "blacklist", timeToLive = 60*60)
public class LogoutToken {

	@Id
	private String accessToken;
}
