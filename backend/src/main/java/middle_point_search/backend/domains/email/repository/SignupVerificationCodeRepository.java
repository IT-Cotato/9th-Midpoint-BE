package middle_point_search.backend.domains.email.repository;

import org.springframework.data.repository.CrudRepository;

import middle_point_search.backend.domains.email.domain.SignupVerificationCode;

public interface SignupVerificationCodeRepository extends CrudRepository<SignupVerificationCode, String> {
}
