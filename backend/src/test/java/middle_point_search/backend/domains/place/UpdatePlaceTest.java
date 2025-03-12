package middle_point_search.backend.domains.place;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import jakarta.persistence.EntityManager;
import middle_point_search.backend.common.BaseIntegrationTest;
import middle_point_search.backend.common.dto.AccessTokenAndRefreshToken;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.repository.MemberRepository;
import middle_point_search.backend.domains.memberRoom.domain.MemberRoom;
import middle_point_search.backend.domains.memberRoom.repository.MemberRoomRepository;
import middle_point_search.backend.domains.place.domain.Place;
import middle_point_search.backend.domains.place.dto.request.UpdatePlaceRequest;
import middle_point_search.backend.domains.place.repository.PlaceRepository;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.repository.RoomRepository;

@DisplayName("장소 수정 테스트")
public class UpdatePlaceTest extends BaseIntegrationTest {

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
	@Autowired
	private EntityManager entityManager;

	@BeforeEach
	void setUp() throws Exception {
		AccessTokenAndRefreshToken accessTokenAndRefreshToken = signupAndLoginMember(false);

		memberEmail = NO_ADDRESS_MEMBER_EMAIL;
		memberAccessToken = accessTokenAndRefreshToken.accessToken();
	}

	@Test
	@DisplayName("장소 수정에 성공한다.")
	void 장소수정성공() throws Exception {
		// given
		// 방 생성
		final String roomId = "roomId";
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
		MemberRoom memberRoom = memberRoomRepository.save(MemberRoom.builder()
			.member(member)
			.room(room)
			.build());

		// 장소 저장
		Place place = placeRepository.save(Place.builder()
			.siDo("siDo")
			.siGunGu("siGunGu")
			.roadNameAddress("roadNameAddress")
			.addressLatitude(1.0)
			.addressLongitude(1.0)
			.member(member)
			.room(room)
			.googlePlaceId("googlePlaceId")
			.build());

		// when
		// 장소 수정
		final String updateSiDo = "서울특별시";
		final String updateSiGunGu = "강남구";
		final String updateRoadNameAddress = "강남대로 123";
		final Double updateAddressLat = 37.123456;
		final Double updateAddressLong = 127.123456;

		UpdatePlaceRequest updatePlaceRequest = new UpdatePlaceRequest(
			place.getId(),
			updateSiDo,
			updateSiGunGu,
			updateRoadNameAddress,
			updateAddressLat,
			updateAddressLong
		);

		ResultActions resultActions = mockMvc.perform(patch("/api/places/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + memberAccessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(updatePlaceRequest)));

		// then
		resultActions
			.andExpect(status().isOk());

		// Place 변경은 JPQL을 사용하므로 영속성 컨텍스트에 반영되지 않아, 기존 place는 영속성 컨텍스트에 제외
		entityManager.detach(place);
		Place updatedPlace = placeRepository.findById(place.getId()).orElseThrow();
		assertEquals(updatedPlace.getSiDo(), updateSiDo);
		assertEquals(updatedPlace.getSiGunGu(), updateSiGunGu);
		assertEquals(updatedPlace.getRoadNameAddress(), updateRoadNameAddress);
		assertEquals(updatedPlace.getAddressLatitude(), updateAddressLat);
		assertEquals(updatedPlace.getAddressLongitude(), updateAddressLong);
	}

	@ParameterizedTest
	@MethodSource("provideUpdatePlaceRequests")
	@DisplayName("장소 수정에 실패한다.")
	void 장소수정실패(UpdatePlaceRequestDTO updatePlaceRequestDto) throws Exception {
		// given
		// 방 생성
		final String roomId = "roomId";
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
		MemberRoom memberRoom = memberRoomRepository.save(MemberRoom.builder()
			.member(member)
			.room(room)
			.build());

		// 장소 저장
		Place place = placeRepository.save(Place.builder()
			.siDo("siDo")
			.siGunGu("siGunGu")
			.roadNameAddress("roadNameAddress")
			.addressLatitude(1.0)
			.addressLongitude(1.0)
			.member(member)
			.room(room)
			.googlePlaceId("googlePlaceId")
			.build());

		// 장소 수정 Request 생성
		UpdatePlaceRequest updatePlaceRequest = new UpdatePlaceRequest(
			place.getId(),
			updatePlaceRequestDto.siDo,
			updatePlaceRequestDto.siGunGu,
			updatePlaceRequestDto.roadNameAddress,
			updatePlaceRequestDto.addressLatitude,
			updatePlaceRequestDto.addressLongitude
		);

		// when
		ResultActions resultActions = mockMvc.perform(patch("/api/places/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + memberAccessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(updatePlaceRequest)));

		// then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("C-202"));
	}

	private static class UpdatePlaceRequestDTO {
		private final String siDo;
		private final String siGunGu;
		private final String roadNameAddress;
		private final Double addressLatitude;
		private final Double addressLongitude;

		public UpdatePlaceRequestDTO(String siDo, String siGunGu, String roadNameAddress, Double addressLatitude,
			Double addressLongitude) {
			this.siDo = siDo;
			this.siGunGu = siGunGu;
			this.roadNameAddress = roadNameAddress;
			this.addressLatitude = addressLatitude;
			this.addressLongitude = addressLongitude;
		}
	}

	private static Stream<UpdatePlaceRequestDTO> provideUpdatePlaceRequests() {
		return Stream.of(
			// 시도가 없는 경우
			new UpdatePlaceRequestDTO(
				"",
				"강남구",
				"강남대로 123",
				37.123456,
				127.123456
			),
			// 시군구가 없는 경우
			new UpdatePlaceRequestDTO(
				"서울특별시",
				"",
				"강남대로 123",
				37.123456,
				127.123456
			),
			// 상세주소가 없는 경우
			new UpdatePlaceRequestDTO(
				"서울특별시",
				"강남구",
				"",
				37.123456,
				127.123456
			),
			// 위도가 없는 경우
			new UpdatePlaceRequestDTO(
				"서울특별시",
				"강남구",
				"강남대로 123",
				null,
				127.123456
			),
			// 경도가 없는 경우
			new UpdatePlaceRequestDTO(
				"서울특별시",
				"강남구",
				"강남대로 123",
				37.123456,
				null
			)
		);
	}

	@Test
	@DisplayName("방에 속해있지 않으면 장소 수정에 실패한다.")
	void 방에_속해있지_않으면_장소수정실패() throws Exception {
		// given
		// 방 생성
		final String roomId = "roomId";
		Room room = Room.builder()
			.id(roomId)
			.name("roomName")
			.memo("roomMemo")
			.build();
		roomRepository.save(room);

		// 회원 id 조회
		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

		// 장소 저장
		Place place = placeRepository.save(Place.builder()
			.siDo("siDo")
			.siGunGu("siGunGu")
			.roadNameAddress("roadNameAddress")
			.addressLatitude(1.0)
			.addressLongitude(1.0)
			.member(member)
			.room(room)
			.googlePlaceId("googlePlaceId")
			.build());

		// when
		// 방에 속해있지 않은 회원으로 장소 수정
		final String updateSiDo = "서울특별시";
		final String updateSiGunGu = "강남구";
		final String updateRoadNameAddress = "강남대로 123";
		final Double updateAddressLat = 37.123456;
		final Double updateAddressLong = 127.123456;

		UpdatePlaceRequest updatePlaceRequest = new UpdatePlaceRequest(
			place.getId(),
			updateSiDo,
			updateSiGunGu,
			updateRoadNameAddress,
			updateAddressLat,
			updateAddressLong
		);

		ResultActions resultActions = mockMvc.perform(patch("/api/places/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + memberAccessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(updatePlaceRequest)));

		// then
		resultActions
			.andExpect(jsonPath("$.code").value("C-203"));
	}
}
