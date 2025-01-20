package middle_point_search.backend.common.dummy;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.dummy.repository.JDBCRepository;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.domain.Role;
import middle_point_search.backend.domains.member.repository.MemberRepository;
import middle_point_search.backend.domains.memberRoom.domain.MemberRoom;
import middle_point_search.backend.domains.place.domain.Place;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.repository.RoomRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataInitService {

	private final PasswordEncoder passwordEncoder;
	private final JDBCRepository JDBCRepository;
	private final MemberRepository memberRepository;
	private final RoomRepository roomRepository;

	private final EntityManager entityManager;

	@Transactional
	public void initializeData() {
		log.info("DataInitService.initializeData ======================");

		// 시간 재기 추후 삭제
		long startTime = System.currentTimeMillis();


		// Members 생성
		List<Member> members = new ArrayList<>();
		String password = passwordEncoder.encode("1234");
		for (int i = 1; i <= 500000; i++) {
			Member member = Member.createWithoutAddress(
				"user" + i + "@test.com",
				password,
				"user" + i,
				Role.USER);
			members.add(member);
		}
		JDBCRepository.saveAllMembers(members);

		members = memberRepository.findAll(); // 엔티티 조회

		// Rooms 생성
		List<Room> rooms = new ArrayList<>();
		for (int i = 1; i <= 500000; i++) {
			Room room = Room.builder()
				.id(String.valueOf(i))
				.name("room" + i)
				.memo("memo" + i)
				.build();
			rooms.add(room);
		}
		JDBCRepository.saveAllRooms(rooms);

		rooms = roomRepository.findAll(); //엔티티 조회

		log.info("Room is managed by JPA: {}", entityManager.contains(rooms.get(0)));

		// MemberRooms 생성
		List<MemberRoom> memberRooms = new ArrayList<>();
		for (int i = 1; i <= 500000; i++) {
			MemberRoom memberRoom = MemberRoom.builder()
				.member(members.get(i - 1))
				.room(rooms.get(i - 1))
				.build();
			memberRooms.add(memberRoom);
		}
		JDBCRepository.saveAllMemberRooms(memberRooms);

		// Places 생성
		List<Place> places = new ArrayList<>();
		for (int i = 1; i <= 500000; i++) {
			Place place = Place.builder()
				.siDo("siDo" + i)
				.siGunGu("siGunGu" + i)
				.roadNameAddress("roadNameAddress" + i)
				.addressLatitude(37.0 + i * 0.0001)
				.addressLongitude(127.0 + i * 0.0001)
				.room(rooms.get(i - 1))
				.member(members.get(i - 1))
				.googlePlaceId("googlePlaceId" + i)
				.build();
			places.add(place);
		}
		entityManager.clear(); // room들이 변경감지되어 place를 저장하기 전에 clear
		JDBCRepository.saveAllPlaces(places);

		// 시간 재기, 추후 삭제
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		log.info("Data initialization took " + duration + " milliseconds");
	}
}
