package middle_point_search.backend.domains.email.service;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.domains.email.domain.PasswordReissueVerificationCode;
import middle_point_search.backend.domains.email.repository.PasswordReissueVerificationCodeRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PasswordReissueVerificationCodeService {

	private final PasswordReissueVerificationCodeRepository passwordReissueVerificationCodeRepository;

	@Transactional
	public void checkEmailCodeDuplicationAndSaveEmailCode(String email, String code) {
		PasswordReissueVerificationCode passwordReissueVerificationCode = passwordReissueVerificationCodeRepository
			.findById(email)
			.orElse(null);

		// 1. 이미 저장된 emailCode가 있으면 code만 변경
		// 2. 없으면 새로 생성
		if (passwordReissueVerificationCode != null) {
			passwordReissueVerificationCode.setCode(code);
		} else {
			passwordReissueVerificationCode = new PasswordReissueVerificationCode(email, code);
		}

		passwordReissueVerificationCodeRepository.save(passwordReissueVerificationCode);
	}

	// 인증 코드 생성
	public String createVerificationCode() {
		Random random = new Random();

		return String.format("%06d", random.nextInt(1000000)); // 000000부터 999999까지의 문자열 생성
	}

	// 인증 코드 확인
	public void verifyEmailCode(String email, String code) throws CustomException {
		PasswordReissueVerificationCode passwordReissueVerificationCode = passwordReissueVerificationCodeRepository.findById(
			email).orElse(null);

		if (passwordReissueVerificationCode == null) {
			throw CustomException.from(REQUIRE_VERIFICATION_REQUEST_FIRST);
		}

		if (!passwordReissueVerificationCode.getCode().equals(code)) {
			throw CustomException.from(VERIFICATION_CODE_NOT_MATCH);
		}

		passwordReissueVerificationCodeRepository.delete(passwordReissueVerificationCode);
	}
}
