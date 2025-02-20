package middle_point_search.backend.common.dummy;

public enum DummyDataConstant {
	MEMBER_COUNT(1),
	ROOM_COUNT(1),
	MEMBER_ROOM_COUNT(1),
	PLACE_COUNT(1),
	PLACE_VOTE_ROOM_COUNT(1),
	PLACE_VOTE_CANDIDATE_COUNT(1),
	PLACE_VOTE_CANDIDATE_MEMBER_COUNT(1),
	TIME_VOTE_ROOM_COUNT(1), 
	MEETING_DATE_COUNT(1),
	TIME_VOTE_COUNT(1);

	final int count;

	DummyDataConstant(int count) {
		this.count = count;
	}
}
