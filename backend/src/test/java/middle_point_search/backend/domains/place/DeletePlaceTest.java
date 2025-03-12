package middle_point_search.backend.domains.place;

import static org.junit.jupiter.api.Assertions.*;
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

@DisplayName("장소 삭제 테스트")
public class DeletePlaceTest extends BaseIntegrationTest {

	private String memberAccessToken;
	private String memberEmail;

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

		memberEmail = NO_ADDRESS_MEMBER_EMAIL;
		memberAccessToken = accessTokenAndRefreshToken.accessToken();
	}

	@Test
	@DisplayName("장소 삭제에 성공한다.")
	void 장소삭제성공() throws Exception {
		// given
		// 방 생성
		String roomId = "roomId";
		Room room = Room.builder()
			.id(roomId)
			.name("roomName")
			.build();
		roomRepository.save(room);

		// 멤버 조회
		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버입니다."));
		// 방에 멤버 저장
		memberRoomRepository.save(MemberRoom.builder()
			.room(room)
			.member(member)
			.build());

		// 장소 생성
		Place place = Place.builder()
			.siDo("siDo")
			.siGunGu("siGunGu")
			.roadNameAddress("roadNameAddress")
			.room(room)
			.addressLatitude(1.0)
			.addressLongitude(1.0)
			.member(member)
			.googlePlaceId("googlePlaceId")
			.build();
		place = placeRepository.save(place);

		// when
		ResultActions resultActions = mockMvc.perform(delete("/api/places/{placeId}", place.getId())
			.header("Authorization", "Bearer " + memberAccessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON));

		// then
		resultActions
			.andExpect(status().isOk());

		// 장소 삭제 확인
		assertFalse(placeRepository.existsById(place.getId()));
	}

	@Test
	@DisplayName("존재하지 않는 장소를 삭제하면 실패한다.")
	void 존재하지않는장소삭제실패() throws Exception {
		// given
		// 방 생성
		String roomId = "roomId";
		Room room = Room.builder()
			.id(roomId)
			.name("roomName")
			.build();
		roomRepository.save(room);

		// 멤버 조회
		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버입니다."));

		// 방에 멤버 저장
		memberRoomRepository.save(MemberRoom.builder()
			.room(room)
			.member(member)
			.build());

		// when
		ResultActions resultActions = mockMvc.perform(delete("/api/places/{placeId}", "422")
			.header("Authorization", "Bearer " + memberAccessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON));

		// then
		resultActions
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("P-201"));
	}

	@Test
	@DisplayName("방에 속하지 않은 멤버가 장소를 삭제하면 실패한다.")
	void 방에속하지않은멤버장소삭제실패() throws Exception {
		// given
		// 방 생성
		String roomId = "roomId";
		Room room = Room.builder()
			.id(roomId)
			.name("roomName")
			.build();
		roomRepository.save(room);

		// 멤버 조회
		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버입니다."));

		// 장소 생성
		Place place = Place.builder()
			.siDo("siDo")
			.siGunGu("siGunGu")
			.roadNameAddress("roadNameAddress")
			.room(room)
			.addressLatitude(1.0)
			.addressLongitude(1.0)
			.member(member)
			.googlePlaceId("googlePlaceId")
			.build();
		place = placeRepository.save(place);

		// when
		ResultActions resultActions = mockMvc.perform(delete("/api/places/{placeId}", place.getId())
			.header("Authorization", "Bearer " + memberAccessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON));

		// then
		resultActions
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("MR-003"));
	}
}
