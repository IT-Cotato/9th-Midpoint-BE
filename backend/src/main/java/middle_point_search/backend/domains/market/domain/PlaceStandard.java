package middle_point_search.backend.domains.market.domain;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import middle_point_search.backend.domains.member.domain.Transport;

@Getter
public enum PlaceStandard {

	ALL("전체", null),
	STUDY("스터디", null),
	CAFE("카페", "CE7"),
	RESTAURANT("식당", "FD6"),
	;

	private final String name;
	private final String code;

	PlaceStandard(String name, String code) {
		this.name = name;
		this.code = code;
	}
}
