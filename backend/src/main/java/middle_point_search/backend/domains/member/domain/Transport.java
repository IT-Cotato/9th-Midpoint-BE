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
}
