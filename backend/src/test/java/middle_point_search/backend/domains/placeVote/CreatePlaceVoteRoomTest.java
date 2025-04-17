package middle_point_search.backend.domains.placeVote;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
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
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteRoom;
import middle_point_search.backend.domains.placeVoteRoom.dto.dto.PlaceCandidateInfo;
import middle_point_search.backend.domains.placeVoteRoom.dto.request.CreatePlaceVoteRoomRequest;
import middle_point_search.backend.domains.placeVoteRoom.repository.PlaceVoteRoomRepository;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.repository.RoomRepository;

@DisplayName("장소 투표 방 생성 테스트")
public class CreatePlaceVoteRoomTest extends BaseIntegrationTest {

	private String accessToken;
	private String memberEmail;

	@Autowired
	private RoomRepository roomRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private MemberRoomRepository memberRoomRepository;
	@Autowired
	private PlaceVoteRoomRepository placeVoteRoomRepository;

	@BeforeEach
	void setUp() throws Exception {
		AccessTokenAndRefreshToken accessTokenAndRefreshToken = signupAndLoginMember(false);

		memberEmail = NO_ADDRESS_MEMBER_EMAIL;
		accessToken = accessTokenAndRefreshToken.accessToken();
	}

	@Test
	@DisplayName("장소 투표 방 생성에 성공한다.")
	void 장소투표방생성성공() throws Exception {
		// given
		// 방 생성
		final String roomId = "roomId";
		Room room = roomRepository.save(Room.builder()
			.id(roomId)
			.name("roomName")
			.memo("roomMemo")
			.build());

		// 회원 id 조회
		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

		// 방에 회원 저장
		memberRoomRepository.save(MemberRoom.builder()
			.room(room)
			.member(member)
			.build());

		CreatePlaceVoteRoomRequest request = new CreatePlaceVoteRoomRequest(
			List.of(
				new PlaceCandidateInfo(
					"장소1",
					"서울",
					"강남구",
					"강남대로 123",
					37.123456,
					127.123456
				),
				new PlaceCandidateInfo(
					"장소2",
					"서울",
					"강남구",
					"역삼로 456",
					37.654321,
					127.654321
				)
			)
		);

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/place-vote-rooms/rooms/{roomId}", roomId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request))
			.header("Authorization", "Bearer " + accessToken)
		);

		// then
		PlaceVoteRoom placeVoteRoom = placeVoteRoomRepository.findByRoom_Id(roomId)
			.orElseThrow(() -> new IllegalArgumentException("장소 투표 방이 존재하지 않습니다."));

		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").value(placeVoteRoom.getId()));
	}

	@Test
	@DisplayName("해당 방의 회원이 아닌 회원이 장소 투표 방을 생성하면 실패한다.")
	void 해당방의회원이아닌회원이장소투표방을생성하면실패() throws Exception {
		// given
		// 방 생성
		final String roomId = "roomId";
		Room room = roomRepository.save(Room.builder()
			.id(roomId)
			.name("roomName")
			.memo("roomMemo")
			.build());

		// when
		// 방에 회원 저장하지 않음

		CreatePlaceVoteRoomRequest request = new CreatePlaceVoteRoomRequest(
			List.of(
				new PlaceCandidateInfo(
					"장소1",
					"서울",
					"강남구",
					"강남대로 123",
					37.123456,
					127.123456
				),
				new PlaceCandidateInfo(
					"장소2",
					"서울",
					"강남구",
					"역삼로 456",
					37.654321,
					127.654321
				)
			)
		);

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/place-vote-rooms/rooms/{roomId}", roomId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request))
			.header("Authorization", "Bearer " + accessToken)
		);

		// then
		resultActions.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("MR-003"));
	}

	@Test
	@DisplayName("이미 장소 투표 방이 존재할 떄 장소 투표 방을 생성하면 실패한다.")
	void 이미장소투표방이존재할때장소투표방을생성하면실패() throws Exception {
		// given
		// 방 생성
		final String roomId = "roomId";
		Room room = roomRepository.save(Room.builder()
			.id(roomId)
			.name("roomName")
			.memo("roomMemo")
			.build());

		// 회원 id 조회
		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

		// 방에 회원 저장
		memberRoomRepository.save(MemberRoom.builder()
			.room(room)
			.member(member)
			.build());

		// 장소 투표방 저장
		placeVoteRoomRepository.save(new PlaceVoteRoom(room));

		CreatePlaceVoteRoomRequest request = new CreatePlaceVoteRoomRequest(
			List.of(
				new PlaceCandidateInfo(
					"장소1",
					"서울",
					"강남구",
					"강남대로 123",
					37.123456,
					127.123456
				),
				new PlaceCandidateInfo(
					"장소2",
					"서울",
					"강남구",
					"역삼로 456",
					37.654321,
					127.654321
				)
			)
		);

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/place-vote-rooms/rooms/{roomId}", roomId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request))
			.header("Authorization", "Bearer " + accessToken)
			.accept(MediaType.APPLICATION_JSON));

		// then
		resultActions.andExpect(status().isConflict())
			.andExpect(jsonPath("$.code").value("V-302"));
	}

	@Test
	@DisplayName("존재하지 않는 방에 장소 투표 방을 생성하면 실패한다.")
	void 존재하지않는방에장소투표방을생성하면실패() throws Exception {
		// given
		final String roomId = "roomId";

		CreatePlaceVoteRoomRequest request = new CreatePlaceVoteRoomRequest(
			List.of(
				new PlaceCandidateInfo(
					"장소1",
					"서울",
					"강남구",
					"강남대로 123",
					37.123456,
					127.123456
				),
				new PlaceCandidateInfo(
					"장소2",
					"서울",
					"강남구",
					"역삼로 456",
					37.654321,
					127.654321
				)
			)
		);

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/place-vote-rooms/rooms/{roomId}", roomId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request))
			.header("Authorization", "Bearer " + accessToken)
			.accept(MediaType.APPLICATION_JSON));

		// then
		resultActions.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("R-201"));
	}

	@ParameterizedTest
	@DisplayName("장소 투표 방 생성 시 잘못된 요청 파라미터가 들어오면 실패한다.")
	@MethodSource("provideInvalidCreatePlaceVoteRoomRequest")
	void 장소투표방생성시잘못된요청파라미터가들어오면실패(
		CreatePlaceVoteRoomRequest createPlaceVoteRoomRequest
	) throws Exception {
		// given
		// 방 생성
		final String roomId = "roomId";
		Room room = roomRepository.save(Room.builder()
			.id(roomId)
			.name("roomName")
			.memo("roomMemo")
			.build());

		// 회원 id 조회
		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

		// 방에 회원 저장
		memberRoomRepository.save(MemberRoom.builder()
			.room(room)
			.member(member)
			.build());

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/place-vote-rooms/rooms/{roomId}", roomId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(createPlaceVoteRoomRequest))
			.header("Authorization", "Bearer " + accessToken)
			.accept(MediaType.APPLICATION_JSON));

		// then
		resultActions.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("C-202"));
	}

	// 장소 투표 방 생성 시 잘못된 요청 파라미터
	private static Stream<CreatePlaceVoteRoomRequest> provideInvalidCreatePlaceVoteRoomRequest() {
		return Stream.of(
			// 빈 리스트
			new CreatePlaceVoteRoomRequest(List.of()),
			// 장소 후보 정보가 없는 경우
			new CreatePlaceVoteRoomRequest(List.of(
				new PlaceCandidateInfo(
					"",
					"서울",
					"강남구",
					"강남대로 123",
					37.123456,
					127.123456
				)
			)),
			new CreatePlaceVoteRoomRequest(List.of(
				new PlaceCandidateInfo(
					"강남역",
					"",
					"강남구",
					"강남대로 123",
					37.123456,
					127.123456
				)
			)),
			new CreatePlaceVoteRoomRequest(List.of(
				new PlaceCandidateInfo(
					"강남역",
					"서울",
					"",
					"강남대로 123",
					37.123456,
					127.123456
				)
			)),
			new CreatePlaceVoteRoomRequest(List.of(
				new PlaceCandidateInfo(
					"강남역",
					"서울",
					"강남구",
					"",
					37.123456,
					127.123456
				)
			)),
			new CreatePlaceVoteRoomRequest(List.of(
				new PlaceCandidateInfo(
					"강남역",
					"서울",
					"강남구",
					"강남대로 123",
					null,
					127.123456
				)
			)),
			new CreatePlaceVoteRoomRequest(List.of(
				new PlaceCandidateInfo(
					"강남역",
					"서울",
					"강남구",
					"강남대로 123",
					37.123456,
					null
				)
			))
		);
	}
}
