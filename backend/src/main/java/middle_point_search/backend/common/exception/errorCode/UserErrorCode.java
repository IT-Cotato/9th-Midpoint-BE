package middle_point_search.backend.common.exception.errorCode;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum UserErrorCode implements ErrorCode {

	//인증 및 토큰 관련
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"A-001", "인증에 실패하였습니다."),
	INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED,"A-002", "Access Token이 유효하지 않습니다."),
	INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED,"A-003", "Refresh Token이 유효하지 않습니다."),
	REISSUE_ACCESS_TOKEN(HttpStatus.PAYMENT_REQUIRED,"A-004", "Access Token을 재발급해야합니다."),
	ACCESS_DENIED(HttpStatus.FORBIDDEN, "A-005", "접근 권한이 없습니다."),
	API_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A-006", "API 인증 정보가 정확하지 않습니다."),

	//회원 관련
	MEMBER_CREDENTIAL_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "M-002", "비밀번호가 일치하지 않습니다"),
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M-201", "존재하지 않는 회원입니다."),

	//방 관련
	ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "R-201", "존재하지 않는 방입니다."),
	ROOM_TYPE_UNPROCESSABLE(HttpStatus.UNPROCESSABLE_ENTITY, "R-001", "방의 타입이 일치하지 않습니다."),

	//장소 관련
	PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "P-201", "방에 입력된 장소가 없습니다."),
	PLACE_CONFLICT(HttpStatus.CONFLICT, "P-302", "이미 장소를 저장하였습니다."),

	//트표 관련
	CANDIDATE_NOT_FOUND(HttpStatus.BAD_REQUEST,  "V-101", "투표 후보가 아닙니다."),
	VOTE_NOT_FOUND(HttpStatus.NOT_FOUND,  "V-201", "투표를 한 적이 없습니다."),
	VOTE_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND,  "V-202", "생성된 투표방이 없습니다."),
	ALREADY_VOTED(HttpStatus.CONFLICT,  "V-301", "이미 투표를 하였습니다."),
	DUPLICATE_VOTE_ROOM(HttpStatus.CONFLICT,  "V-302", "이미 투표방이 존재합니다."),

	//서버 관련
	API_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S-001", "API 서버에 문제가 발생하였습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	UserErrorCode(HttpStatus httpStatus, String code, String message) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.message = message;
	}

	@Override
	public HttpStatus getHttpStatus() {
		return this.httpStatus;
	}
	@Override
	public String getCode() {
		return this.code;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

}
