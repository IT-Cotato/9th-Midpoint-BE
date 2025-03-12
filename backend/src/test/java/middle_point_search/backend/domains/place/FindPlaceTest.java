package middle_point_search.backend.domains.place;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import middle_point_search.backend.common.BaseIntegrationTest;
import middle_point_search.backend.common.dto.AccessTokenAndRefreshToken;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.repository.MemberRepository;
import middle_point_search.backend.domains.memberRoom.domain.MemberRoom;
import middle_point_search.backend.domains.memberRoom.repository.MemberRoomRepository;
import middle_point_search.backend.domains.place.domain.Place;
import middle_point_search.backend.domains.place.repository.PlaceRepository;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.repository.RoomRepository;

@DisplayName("장소 찾기 테스트")
public class FindPlaceTest extends BaseIntegrationTest {

	private String memberAccessToken;
	private String memberEmail;
	private String friendEmail;

	@Autowired
	private MemberRoomRepository memberRoomRepository;
	@Autowired
	private RoomRepository roomRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private PlaceRepository placeRepository;

	@BeforeEach
	void setUp() throws Exception {
		AccessTokenAndRefreshToken accessTokenAndRefreshToken = signupAndLoginMember(false);
		signupAndLoginMember(true);

		memberEmail = NO_ADDRESS_MEMBER_EMAIL;
		memberAccessToken = accessTokenAndRefreshToken.accessToken();

		friendEmail = ADDRESS_MEMBER_EMAIL;
	}

	// 내가 저장한 장소 조회를 성공한다.
	@Test
	@DisplayName("내가 저장한 장소 조회를 성공한다.")
	void 내가_저장한_장소_조회 () throws Exception {
		// given
		// 방 생성
		String roomId = "roomId";
		Room room = Room.builder()
			.id(roomId)
			.name("roomName")
			.memo("roomMemo")
			.build();
		roomRepository.save(room);

		// 회원 id 조회
		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

		// 방에 회원 저장
		memberRoomRepository.save(MemberRoom.builder()
			.member(member)
			.room(room)
			.build());

		// 장소 저장
		final String siDo = "서울특별시";
		final String siGunGu = "강남구";
		final String roadNameAddress = "강남대로 123";
		final Double addressLatitude = 37.123456;
		final Double addressLongitude = 127.123456;

		placeRepository.save(Place.builder()
			.siDo(siDo)
			.siGunGu(siGunGu)
			.roadNameAddress(roadNameAddress)
			.addressLatitude(addressLatitude)
			.addressLongitude(addressLongitude)
			.room(room)
			.member(member)
			.googlePlaceId("googlePlaceId")
			.build());
		// when
		ResultActions resultActions = mockMvc.perform(get("/api/places/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + memberAccessToken)
			.accept(MediaType.APPLICATION_JSON));

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.myLocationExistence").value(true))
			.andExpect(jsonPath("$.data.myLocations[0].placeId").exists())
			.andExpect(jsonPath("$.data.myLocations[0].siDo").value(siDo))
			.andExpect(jsonPath("$.data.myLocations[0].siGunGu").value(siGunGu))
			.andExpect(jsonPath("$.data.myLocations[0].roadNameAddress").value(roadNameAddress))
			.andExpect(jsonPath("$.data.myLocations[0].addressLat").value(addressLatitude))
			.andExpect(jsonPath("$.data.myLocations[0].addressLong").value(addressLongitude));
	}

	// 친구가 저장한 장소 조회를 성공한다.
	@Test
	@DisplayName("친구가 저장한 장소 조회를 성공한다.")
	void 친구가_저장한_장소_조회 () throws Exception {
		// given
		// 방 생성
		String roomId = "roomId";
		Room room = Room.builder()
			.id(roomId)
			.name("roomName")
			.memo("roomMemo")
			.build();
		roomRepository.save(room);

		// 회원 id 조회
		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
		Member friend = memberRepository.findByEmail(friendEmail)
			.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

		// 방에 회원 저장
		memberRoomRepository.save(MemberRoom.builder()
			.member(member)
			.room(room)
			.build());
		memberRoomRepository.save(MemberRoom.builder()
			.member(friend)
			.room(room)
			.build());

		// 장소 저장
		final String siDo = "서울특별시";
		final String siGunGu = "강남구";
		final String roadNameAddress = "강남대로 123";
		final Double addressLatitude = 37.123456;
		final Double addressLongitude = 127.123456;

		placeRepository.save(Place.builder()
			.siDo(siDo)
			.siGunGu(siGunGu)
			.roadNameAddress(roadNameAddress)
			.addressLatitude(addressLatitude)
			.addressLongitude(addressLongitude)
			.room(room)
			.member(friend)
			.googlePlaceId("googlePlaceId")
			.build());
		// when
		ResultActions resultActions = mockMvc.perform(get("/api/places/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + memberAccessToken)
			.accept(MediaType.APPLICATION_JSON));

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.friendLocationExistence").value(true))
			.andExpect(jsonPath("$.data.friendLocations[0].siDo").value(siDo))
			.andExpect(jsonPath("$.data.friendLocations[0].siGunGu").value(siGunGu))
			.andExpect(jsonPath("$.data.friendLocations[0].roadNameAddress").value(roadNameAddress))
			.andExpect(jsonPath("$.data.friendLocations[0].addressLat").value(addressLatitude))
			.andExpect(jsonPath("$.data.friendLocations[0].addressLong").value(addressLongitude));
	}

	// 내가 저장한 장소와 친구가 저장한 장소가 모두 없을 때 조회를 성공한다.
	@Test
	@DisplayName("내가 저장한 장소와 친구가 저장한 장소가 모두 없을 때 조회를 성공한다.")
	void 내가_저장한_장소와_친구가_저장한_장소가_모두_없을_때_조회 () throws Exception {
		// given
		// 방 생성
		String roomId = "roomId";
		Room room = Room.builder()
			.id(roomId)
			.name("roomName")
			.memo("roomMemo")
			.build();
		roomRepository.save(room);

		// 회원 조회
		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

		// 방에 멤머 버장
		memberRoomRepository.save(MemberRoom.builder()
			.member(member)
			.room(room)
			.build());

		// when
		ResultActions resultActions = mockMvc.perform(get("/api/places/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + memberAccessToken)
			.accept(MediaType.APPLICATION_JSON));

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.myLocationExistence").value(false))
			.andExpect(jsonPath("$.data.friendLocationExistence").value(false));
	}

	@Test
	@DisplayName("방에 속하지 않은 회원이 장소 조회를 실패한다.")
	void 방에_속하지_않은_회원이_장소_조회 () throws Exception {
		// given
		// 방 생성
		String roomId = "roomId";
		Room room = Room.builder()
			.id(roomId)
			.name("roomName")
			.memo("roomMemo")
			.build();
		roomRepository.save(room);

		// 회원 조회
		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

		// when
		ResultActions resultActions = mockMvc.perform(get("/api/places/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + memberAccessToken)
			.accept(MediaType.APPLICATION_JSON));

		// then
		resultActions
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("MR-003"));
	}
}
