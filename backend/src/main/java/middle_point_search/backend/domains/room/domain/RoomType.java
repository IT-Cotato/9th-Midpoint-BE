package middle_point_search.backend.domains.room.domain;

import static middle_point_search.backend.common.exception.errorCode.CommonErrorCode.*;

import java.util.Arrays;
import java.util.Objects;

import middle_point_search.backend.common.exception.CustomException;

public enum RoomType {
	TOGETHER("TOGETHER"),
	SELF("SELF"),
	DISABLED("DISABLED");

	private String roomName;

	RoomType(String roomName) {
		this.roomName = roomName;
	}

	public static RoomType getRoomTypeByName(String name) {
		return Arrays.stream(RoomType.values())
			.filter(roomType -> Objects.equals(roomType.roomName, name))
			.findFirst()
			.orElseThrow(() -> new CustomException(BAD_REQUEST));
	}
}
