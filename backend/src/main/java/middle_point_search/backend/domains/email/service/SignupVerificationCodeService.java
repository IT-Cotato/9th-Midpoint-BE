package middle_point_search.backend.domains.email.service;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.domains.email.domain.SignupVerificationCode;
import middle_point_search.backend.domains.email.repository.SignupVerificationCodeRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SignupVerificationCodeService {

	private final SignupVerificationCodeRepository signupVerificationCodeRepository;

	@Transactional
	public void checkEmailCodeDuplicationAndSaveEmailCode(String email, String code) {
		signupVerificationCodeRepository.findById(email)
			.ifPresentOrElse(
				existing -> existing.setCode(code),
				() -> signupVerificationCodeRepository.save(new SignupVerificationCode(email, code))
			);
	}

	// 인증 코드 생성
	public String createVerificationCode() {
		return String.format("%06d", new Random().nextInt(1000000)); // 000000부터 999999까지의 문자열 생성
	}

	// 인증 코드 확인
	public boolean verifyEmailCode(String email, String code) throws CustomException {
		SignupVerificationCode signupVerificationCode = signupVerificationCodeRepository.findById(email)
			.orElseThrow(() -> CustomException.from(REQUIRE_VERIFICATION_REQUEST_FIRST));

		return signupVerificationCode.getCode().equals(code);
	}

	// 인증 코드 판별 후 삭제
	@Transactional
	public void validateEmailCodeAndDelete(String email, String code) {
		if (!verifyEmailCode(email, code)) {
			throw CustomException.from(VERIFICATION_CODE_NOT_MATCH);
		}

		signupVerificationCodeRepository.deleteById(email);
	}
}
