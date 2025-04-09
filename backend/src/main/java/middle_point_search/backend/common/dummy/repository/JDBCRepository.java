package middle_point_search.backend.common.dummy.repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.dummy.dto.MeetingDateDummyDto;
import middle_point_search.backend.common.dummy.dto.MemberDummyDto;
import middle_point_search.backend.common.dummy.dto.MemberRoomDummyDto;
import middle_point_search.backend.common.dummy.dto.PlaceDummyDto;
import middle_point_search.backend.common.dummy.dto.PlaceVoteCandidateDummyDto;
import middle_point_search.backend.common.dummy.dto.PlaceVoteDummyDto;
import middle_point_search.backend.common.dummy.dto.PlaceVoteRoomDummyDto;
import middle_point_search.backend.common.dummy.dto.RoomDummyDto;
import middle_point_search.backend.common.dummy.dto.TimeVoteDummyDto;
import middle_point_search.backend.common.dummy.dto.TimeVoteRoomDummyDto;

@Repository
@RequiredArgsConstructor
public class JDBCRepository {

	private final JdbcTemplate jdbcTemplate;

	// 모든 멤버 bulk 저장
	public void saveAllMembers(List<MemberDummyDto> members) {
		String sql = "INSERT INTO member (email, pw, name, role, exist_address) " +
			"VALUES (?, ?, ?, ?, ?)";

		jdbcTemplate.batchUpdate(sql,
			members,
			members.size(),
			(PreparedStatement ps, MemberDummyDto member) -> {
				ps.setString(1, member.email());
				ps.setString(2, member.password());
				ps.setString(3, member.name());
				ps.setString(4, member.role().name());
				ps.setBoolean(5, false);
			});
	}

	// 모든 방 bulk 저장
	public void saveAllRooms(List<RoomDummyDto> rooms) {
		String sql = "INSERT INTO room (room_id, room_name, room_memo) " +
			"VALUES (?, ?, ?)";

		jdbcTemplate.batchUpdate(sql,
			rooms,
			rooms.size(),
			(PreparedStatement ps, RoomDummyDto room) -> {
				ps.setString(1, room.roomId());
				ps.setString(2, room.name());
				ps.setString(3, room.memo());
			});
	}

	// 모든 멤버 방 bulk 저장
	public void saveAllMemberRooms(List<MemberRoomDummyDto> memberRooms) {
		String sql = "INSERT INTO member_room (member_id, room_id) " +
			"VALUES (?, ?)";

		jdbcTemplate.batchUpdate(sql,
			memberRooms,
			memberRooms.size(),
			(PreparedStatement ps, MemberRoomDummyDto memberRoom) -> {
				ps.setLong(1, memberRoom.memberId());
				ps.setString(2, memberRoom.roomId());
			});
	}

	// 모든 Place bulk 저장
	public void saveAllPlaces(List<PlaceDummyDto> places) {
		String sql =
			"INSERT INTO place (si_do, si_gun_gu, road_name_address, address_latitude, address_longitude, room_id, member_id, google_place_id) "
				+
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		jdbcTemplate.batchUpdate(sql,
			places,
			places.size(),
			(PreparedStatement ps, PlaceDummyDto place) -> {
				ps.setString(1, place.siDo());
				ps.setString(2, place.siGunGu());
				ps.setString(3, place.roadNameAddress());
				ps.setDouble(4, place.addressLatitude());
				ps.setDouble(5, place.addressLongitude());
				ps.setString(6, place.roomId());
				ps.setLong(7, place.memberId());
				ps.setString(8, place.googlePlaceId());
			});
	}

	// 모든 PlaceVoteRoom bulk 저장
	public void saveAllPlaceVoteRooms(List<PlaceVoteRoomDummyDto> placeVoteRooms) {
		String sql = "INSERT INTO place_vote_room (room_id) " +
			"VALUES (?)";

		jdbcTemplate.batchUpdate(sql,
			placeVoteRooms,
			placeVoteRooms.size(),
			(PreparedStatement ps, PlaceVoteRoomDummyDto placeVoteRoom) -> {
				ps.setString(1, placeVoteRoom.roomId());
			});
	}

	// 모든 PlaceVoteCandidate bulk 저장
	public void saveAllPlaceVoteCandidates(List<PlaceVoteCandidateDummyDto> placeVoteCandidates) {
		String sql = "INSERT INTO place_vote_candidate (name, si_do, si_gun_gu, road_name_address, address_latitude, address_longitude, place_vote_room_id) " +
			"VALUES (?, ?, ?, ?, ?, ?, ?)";

		jdbcTemplate.batchUpdate(sql,
			placeVoteCandidates,
			placeVoteCandidates.size(),
			(PreparedStatement ps, PlaceVoteCandidateDummyDto placeVoteCandidate) -> {
				ps.setString(1, placeVoteCandidate.name());
				ps.setString(2, placeVoteCandidate.siDo());
				ps.setString(3, placeVoteCandidate.siGunGu());
				ps.setString(4, placeVoteCandidate.roadNameAddress());
				ps.setDouble(5, placeVoteCandidate.addressLatitude());
				ps.setDouble(6, placeVoteCandidate.addressLongitude());
				ps.setLong(7, placeVoteCandidate.placeVoteRoomId());
			});
	}

	// 모든 PlaceVoteCandidateMember bulk 저장
	public void saveAllPlaceMembers(List<PlaceVoteDummyDto> placeVoteMembers) {
		String sql = "INSERT INTO place_vote (member_id, place_vote_candidate_id) " +
			"VALUES (?, ?)";

		jdbcTemplate.batchUpdate(sql,
			placeVoteMembers,
			placeVoteMembers.size(),
			(PreparedStatement ps, PlaceVoteDummyDto placeVoteCandidateMember) -> {
				ps.setLong(1, placeVoteCandidateMember.memberId());
				ps.setLong(2, placeVoteCandidateMember.placeVoteCandidateId());
			});
	}

	// 모든 TimeVoteRoom bulk 저장
	public void saveAllTimeVoteRooms(List<TimeVoteRoomDummyDto> timeVoteRooms) {
		String sql = "INSERT INTO time_vote_room (room_id) " +
			"VALUES (?)";

		jdbcTemplate.batchUpdate(sql,
			timeVoteRooms,
			timeVoteRooms.size(),
			(PreparedStatement ps, TimeVoteRoomDummyDto timeVoteRoom) -> {
				ps.setString(1, timeVoteRoom.roomId());
			});
	}

	// 모든 MeetingDate bulk 저장
	public void saveAllMeetingDates(List<MeetingDateDummyDto> meetingDates) {
		String sql = "INSERT INTO meeting_date (time_vote_room_id, date) " +
			"VALUES (?, ?)";

		jdbcTemplate.batchUpdate(sql,
			meetingDates,
			meetingDates.size(),
			(PreparedStatement ps, MeetingDateDummyDto meetingDate) -> {
				ps.setLong(1, meetingDate.timeVoteRoomId());
				ps.setDate(2, Date.valueOf(meetingDate.date()));
			});
	}

	// 모든 TimeVote bulk 저장
	public void saveAllTimeVotes(List<TimeVoteDummyDto> timeVotes) {
		String sql = "INSERT INTO time_vote (time_vote_room_id, meeting_date_id, member_id, member_available_start_time, member_available_end_time) " +
			"VALUES (?, ?, ?, ?, ?)";

		jdbcTemplate.batchUpdate(sql,
			timeVotes,
			timeVotes.size(),
			(PreparedStatement ps, TimeVoteDummyDto timeVote) -> {
				ps.setLong(1, timeVote.timeVoteRoomId());
				ps.setLong(2, timeVote.meetingDateId());
				ps.setLong(3, timeVote.memberId());
				ps.setTimestamp(4, Timestamp.valueOf(timeVote.memberAvailableStartTime()));
				ps.setTimestamp(5, Timestamp.valueOf(timeVote.memberAvailableEndTime()));
			});
	}
}
