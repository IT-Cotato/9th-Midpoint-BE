package middle_point_search.backend.domains.member.domain;

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
