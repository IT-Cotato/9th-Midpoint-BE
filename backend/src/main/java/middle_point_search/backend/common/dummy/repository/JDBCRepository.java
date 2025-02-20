package middle_point_search.backend.common.dummy.repository;

import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.dummy.dto.MemberDummyDto;
import middle_point_search.backend.common.dummy.dto.MemberRoomDummyDto;
import middle_point_search.backend.common.dummy.dto.PlaceDummyDto;
import middle_point_search.backend.common.dummy.dto.RoomDummyDto;

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

	// 모든 멤버 삭제
	public void deleteAllMembers() {
		String sql = "DELETE FROM member";
		jdbcTemplate.update(sql);
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
}
