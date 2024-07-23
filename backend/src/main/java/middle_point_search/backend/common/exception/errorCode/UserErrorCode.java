package middle_point_search.backend.common.exception.errorCode;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum UserErrorCode implements ErrorCode {

	//4xx
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증에 실패하였습니다."),
	INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "Access Token이 유효하지 않습니다."),
	INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Refresh Token이 유효하지 않습니다."),
	ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
	MEMBER_CREDENTIAL_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다"),
	API_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "API 인증 정보가 정확하지 않습니다."),
	ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 방입니다."),
	PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "방에 입력된 장소가 없습니다."),
	PLACE_CONFLICT(HttpStatus.CONFLICT, "이미 장소를 저장하였습니다."),

	VOTE_NOT_FOUND(HttpStatus.NOT_FOUND, "투표를 한 적이 없습니다."),
	VOTE_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "생성된 투표방이 없습니다."),
	CANDIDATE_NOT_FOUND(HttpStatus.NOT_FOUND, "투표 후보가 아닙니다."),
	ALREADY_VOTED(HttpStatus.CONFLICT, "이미 투표를 하였습니다."),
	DUPLICATE_VOTE_ROOM(HttpStatus.CONFLICT, "이미 장소투표방이 존재합니다."),

	//5xx
	API_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "API 서버에 문제가 발생하였습니다.")

	;

	private final HttpStatus httpStatus;
	private final String message;

	UserErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

	@Override
	public HttpStatus getHttpStatus() {
		return this.httpStatus;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

}
