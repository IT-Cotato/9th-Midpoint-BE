package middle_point_search.backend.domains.member.domain;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

@Getter
public enum Transport {
	CAR("자동차"),
	PUBLIC("대중교통"),
	;

	private final String name;

	Transport(String name) {
		this.name = name;
	}

	//대소문자 구분 없이 Enum 직렬화 하기
	@JsonCreator
	public static Transport from(String value) {
		return Arrays.stream(Transport.values())
			.filter(transport -> value.equalsIgnoreCase(transport.name())) //대소문자 무시
			.findAny()
			.orElse(null);
	}

	//소문자로 Enum 역직렬화 하기
	@JsonValue
	public String toValue() {
		return name().toLowerCase();
	}
}
