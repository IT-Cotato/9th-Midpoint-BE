package middle_point_search.backend.common.dummy.repository;

import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.memberRoom.domain.MemberRoom;
import middle_point_search.backend.domains.place.domain.Place;
import middle_point_search.backend.domains.room.domain.Room;

@Repository
@RequiredArgsConstructor
public class JDBCRepository {

	private final JdbcTemplate jdbcTemplate;

	// 모든 멤버 bulk 저장
	public void saveAllMembers(List<Member> members) {
		String sql = "INSERT INTO member (email, pw, name, role, exist_address) " +
			"VALUES (?, ?, ?, ?, ?)";

		jdbcTemplate.batchUpdate(sql,
			members,
			members.size(),
			(PreparedStatement ps, Member member) -> {
				ps.setString(1, member.getEmail());
				ps.setString(2, member.getPw());
				ps.setString(3, member.getName());
				ps.setString(4, member.getRole().name());
				ps.setBoolean(5, member.getExistAddress());
			});
	}

	// 모든 방 bulk 저장
	public void saveAllRooms(List<Room> rooms) {
		String sql = "INSERT INTO room (room_id, room_name, room_memo) " +
			"VALUES (?, ?, ?)";

		jdbcTemplate.batchUpdate(sql,
			rooms,
			rooms.size(),
			(PreparedStatement ps, Room room) -> {
				ps.setString(1, room.getId());
				ps.setString(2, room.getName());
				ps.setString(3, room.getMemo());
			});
	}

	// 모든 멤버 방 bulk 저장
	public void saveAllMemberRooms(List<MemberRoom> memberRooms) {
		String sql = "INSERT INTO member_room (member_id, room_id) " +
			"VALUES (?, ?)";

		jdbcTemplate.batchUpdate(sql,
			memberRooms,
			memberRooms.size(),
			(PreparedStatement ps, MemberRoom memberRoom) -> {
				ps.setLong(1, memberRoom.getMember().getId());
				ps.setString(2, memberRoom.getRoom().getId());
			});
	}

	// 모든 Place bulk 저장
	public void saveAllPlaces(List<Place> places) {
		String sql = "INSERT INTO place (si_do, si_gun_gu, road_name_address, address_latitude, address_longitude, room_id, member_id, google_place_id) " +
			"VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		jdbcTemplate.batchUpdate(sql,
			places,
			places.size(),
			(PreparedStatement ps, Place place) -> {
				ps.setString(1, place.getSiDo());
				ps.setString(2, place.getSiGunGu());
				ps.setString(3, place.getRoadNameAddress());
				ps.setDouble(4, place.getAddressLatitude());
				ps.setDouble(5, place.getAddressLongitude());
				ps.setString(6, place.getRoom().getId());
				ps.setLong(7, place.getMember().getId());
				ps.setString(8, place.getGooglePlaceId());
			});
	}
}
