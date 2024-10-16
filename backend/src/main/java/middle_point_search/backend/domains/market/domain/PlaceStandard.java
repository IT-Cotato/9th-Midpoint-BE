package middle_point_search.backend.domains.market.domain;

import lombok.Getter;

@Getter
public enum PlaceStandard {

	ALL("전체", null),
	STUDY("스터디", null),
	CAFE("카페", "CE7"),
	RESTAURANT("음식점", "FD6"),
	;

	private final String name;
	private final String code;

	PlaceStandard(String name, String code) {
		this.name = name;
		this.code = code;
	}
}
