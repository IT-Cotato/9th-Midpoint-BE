package middle_point_search.backend.common.dummy;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.dummy.dto.MemberDummyDto;
import middle_point_search.backend.common.dummy.dto.MemberRoomDummyDto;
import middle_point_search.backend.common.dummy.dto.PlaceDummyDto;
import middle_point_search.backend.common.dummy.dto.RoomDummyDto;
import middle_point_search.backend.common.dummy.repository.JDBCRepository;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.domain.Role;
import middle_point_search.backend.domains.member.repository.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataInitService {

	private final PasswordEncoder passwordEncoder;
	private final JDBCRepository jdbcRepository;
	private final MemberRepository memberRepository;

	public void initializeData() {
		log.info("DataInitService.initializeData ======================");

		// 시간 재기 추후 삭제
		long startTime = System.currentTimeMillis();

		// Members 생성
		List<MemberDummyDto> memberDtos = new ArrayList<>();
		String password = passwordEncoder.encode("1234");
		for (int i = 1; i <= DummyDataConstant.MEMBER_ROOM_COUNT.count; i++) {
			MemberDummyDto member = new MemberDummyDto(
				"user" + i + "@test.com",
				password,
				"user" + i,
				Role.USER);
			memberDtos.add(member);
		}
		jdbcRepository.saveAllMembers(memberDtos);

		List<Member> members = memberRepository.findAll(); // 엔티티 조회

		// Rooms 생성
		List<RoomDummyDto> rooms = new ArrayList<>();
		for (int i = 1; i <= DummyDataConstant.ROOM_COUNT.count; i++) {
			RoomDummyDto room = new RoomDummyDto(
				String.valueOf(i),
				"room" + i,
				"memo" + i
			);
			rooms.add(room);
		}
		jdbcRepository.saveAllRooms(rooms);

		// MemberRooms 생성
		List<MemberRoomDummyDto> memberRooms = new ArrayList<>();
		for (int i = 1; i <= DummyDataConstant.MEMBER_ROOM_COUNT.count; i++) {
			MemberRoomDummyDto memberRoom = new MemberRoomDummyDto(
				(long)i,
				String.valueOf(i)
			);
			memberRooms.add(memberRoom);
		}
		jdbcRepository.saveAllMemberRooms(memberRooms);

		// Places 생성
		List<PlaceDummyDto> places = new ArrayList<>();
		for (int i = 1; i <= DummyDataConstant.PLACE_COUNT.count; i++) {
			PlaceDummyDto place = new PlaceDummyDto(
				"siDo" + i,
				"siGunGu" + i,
				"roadNameAddress" + i,
				37.0 + i * 0.0001,
				127.0 + i * 0.0001,
				String.valueOf(i),
				(long)i,
				"googlePlaceId" + i
			);
			places.add(place);
		}
		jdbcRepository.saveAllPlaces(places);

		// 시간 재기, 추후 삭제
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		log.info("Data initialization took " + duration + " milliseconds");
	}
}
