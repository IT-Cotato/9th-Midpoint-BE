package middle_point_search.backend.domains.email.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.domains.email.util.EmailUtil;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmailService {

	private final EmailUtil emailUtil;

	private static final String EMAIL_TITLE = "SyncSpot 회원가입 인증 코드입니다.";
	private static final String EMAIL_VERIFICATION_NOTICE_TEXT = "인증 코드는 %s 입니다.";
	private static final String EMAIL_NEW_PASSWORD_NOTICE_TEXT = "임시 비밀번호는 %s 입니다. 로그인 후 비밀번호를 변경해주세요.";

	// 인증번호 이메일 보내기
	public void sendVerificationCodeEmail(String email, String code) {
		String text = String.format(EMAIL_VERIFICATION_NOTICE_TEXT, code);
		emailUtil.sendEmail(email, EMAIL_TITLE, text);
	}
}
