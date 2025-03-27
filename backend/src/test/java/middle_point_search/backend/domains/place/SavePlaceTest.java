package middle_point_search.backend.domains.place;

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

import middle_point_search.backend.common.BaseIntegrationTest;
import middle_point_search.backend.common.dto.AccessTokenAndRefreshToken;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.repository.MemberRepository;
import middle_point_search.backend.domains.memberRoom.domain.MemberRoom;
import middle_point_search.backend.domains.memberRoom.repository.MemberRoomRepository;
import middle_point_search.backend.domains.place.dto.request.SavePlaceRequest;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.repository.RoomRepository;

@DisplayName("장소 저장 테스트")
public class SavePlaceTest extends BaseIntegrationTest {

	private String memberAccessToken;
	private String memberEmail;

	@Autowired
	private MemberRoomRepository memberRoomRepository;
	@Autowired
	private RoomRepository roomRepository;
	@Autowired
	private MemberRepository memberRepository;

	@BeforeEach
	void setUp() throws Exception {
		AccessTokenAndRefreshToken accessTokenAndRefreshToken = signupAndLoginMember(false);

		memberEmail = NO_ADDRESS_MEMBER_EMAIL;
		memberAccessToken = accessTokenAndRefreshToken.accessToken();
	}

	@Test
	@DisplayName("장소 저장에 성공한다.")
	void 장소저장성공() throws Exception {
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
		MemberRoom memberRoom = memberRoomRepository.save(MemberRoom.builder()
			.member(member)
			.room(room)
			.build());

		// when
		// 장소 저장
		SavePlaceRequest savePlaceRequest = new SavePlaceRequest(
			"서울특별시",
			"강남구",
			"강남대로 123",
			37.123456,
			127.123456
		);

		ResultActions resultActions = mockMvc.perform(post("/api/places/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + memberAccessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(savePlaceRequest)));

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.data").exists())
			.andExpect(jsonPath("$.data.placeId").isNotEmpty());
	}

	@ParameterizedTest
	@MethodSource("provideSavePlaceRequests")
	@DisplayName("장소 저장에 실패한다.")
	void 장소저장실패(SavePlaceRequest savePlaceRequest) throws Exception {
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
		MemberRoom memberRoom = memberRoomRepository.save(MemberRoom.builder()
			.member(member)
			.room(room)
			.build());

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/places/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + memberAccessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(savePlaceRequest)));

		// then
		resultActions.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("C-202"));
	}

	private static Stream<SavePlaceRequest> provideSavePlaceRequests() {
		return Stream.of(
			// 시도가 없는 경우
			new SavePlaceRequest(
				"",
				"강남구",
				"강남대로 123",
				37.123456,
				127.123456
			),
			// 시군구가 없는 경우
			new SavePlaceRequest(
				"서울특별시",
				"",
				"강남대로 123",
				37.123456,
				127.123456
			),
			// 상세주소가 없는 경우
			new SavePlaceRequest(
				"서울특별시",
				"강남구",
				"",
				37.123456,
				127.123456
			),
			// 위도가 없는 경우
			new SavePlaceRequest(
				"서울특별시",
				"강남구",
				"강남대로 123",
				null,
				127.123456
			),
			// 경도가 없는 경우
			new SavePlaceRequest(
				"서울특별시",
				"강남구",
				"강남대로 123",
				37.123456,
				null
			)
		);
	}

	@Test
	@DisplayName("회원이 방에 속해있지 않으면 장소 저장에 실패한다.")
	void 회원이_방에_속해있지_않으면_장소저장실패() throws Exception {
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

		// when
		// 장소 저장
		SavePlaceRequest savePlaceRequest = new SavePlaceRequest(
			"서울특별시",
			"강남구",
			"강남대로 123",
			37.123456,
			127.123456
		);

		ResultActions resultActions = mockMvc.perform(post("/api/places/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + memberAccessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(savePlaceRequest)));

		// then
		resultActions.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("MR-003"));
	}

	@Test
	@DisplayName("API 호출시 문제가 발생하면 장소 저장에 실패한다.")
	void API호출시_문제가_발생하면_장소저장실패() throws Exception {
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
		MemberRoom memberRoom = memberRoomRepository.save(MemberRoom.builder()
			.member(member)
			.room(room)
			.build());

		// when
		// 장소 저장
		SavePlaceRequest savePlaceRequest = new SavePlaceRequest(
			"서울특별시",
			"강남구",
			"강남대로 123",
			100.123456, // 잘못된 위도
			127.123456 // 잘못된 경도
		);

		ResultActions resultActions = mockMvc.perform(post("/api/places/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + memberAccessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(savePlaceRequest)));

		// then
		resultActions.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.code").value("AS-001"));
	}
}
