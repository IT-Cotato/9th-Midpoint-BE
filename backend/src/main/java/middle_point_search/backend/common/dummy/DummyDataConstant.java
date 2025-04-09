package middle_point_search.backend.common.dummy;

public enum DummyDataConstant {
	MEMBER_COUNT(500000),
	ROOM_COUNT(500000),
	MEMBER_ROOM_COUNT(500000),
	PLACE_COUNT(500000),
	PLACE_VOTE_ROOM_COUNT(500000),
	PLACE_VOTE_CANDIDATE_COUNT(500000),
	PLACE_VOTE_CANDIDATE_MEMBER_COUNT(500000),
	TIME_VOTE_ROOM_COUNT(500000),
	MEETING_DATE_COUNT(500000),
	TIME_VOTE_COUNT(500000);

	final int count;

	DummyDataConstant(int count) {
		this.count = count;
	}
}
