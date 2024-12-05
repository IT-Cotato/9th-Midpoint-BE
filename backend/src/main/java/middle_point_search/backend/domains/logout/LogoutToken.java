package middle_point_search.backend.domains.logout;

import static lombok.AccessLevel.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@RedisHash(value= "blacklist", timeToLive = 60*60)
public class LogoutToken {

	@Id
	private String accessToken;
}
