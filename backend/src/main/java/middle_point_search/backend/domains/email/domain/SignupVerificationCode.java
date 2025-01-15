package middle_point_search.backend.domains.email.domain;

import static lombok.AccessLevel.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@RedisHash(value = "signupVerificationCode", timeToLive = 5 * 60)
public class SignupVerificationCode {

	@Id
	private String email;

	@Setter
	private String code;
}
