package middle_point_search.backend.domains.email.repository;

import org.springframework.data.repository.CrudRepository;

import middle_point_search.backend.domains.email.domain.PasswordReissueVerificationCode;

public interface PasswordReissueVerificationCodeRepository  extends CrudRepository<PasswordReissueVerificationCode, String> {
}
