package middle_point_search.backend.domains.placeVote;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import middle_point_search.backend.common.BaseIntegrationTest;
import middle_point_search.backend.common.dto.AccessTokenAndRefreshToken;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.repository.MemberRepository;
import middle_point_search.backend.domains.memberRoom.domain.MemberRoom;
import middle_point_search.backend.domains.memberRoom.repository.MemberRoomRepository;
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteCandidate;
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteRoom;
import middle_point_search.backend.domains.placeVoteRoom.dto.dto.PlaceCandidateInfo;
import middle_point_search.backend.domains.placeVoteRoom.repository.PlaceVoteCandidateRepository;
import middle_point_search.backend.domains.placeVoteRoom.repository.PlaceVoteRoomRepository;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.repository.RoomRepository;

@DisplayName("장소 투표방 조회 테스트")
public class FindPlaceVoteCandidatesTest extends BaseIntegrationTest {

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
	@Autowired
	private PlaceVoteCandidateRepository placeVoteCandidateRepository;

	@BeforeEach
	void setUp() throws Exception {
		AccessTokenAndRefreshToken accessTokenAndRefreshToken = signupAndLoginMember(false);

		memberEmail = NO_ADDRESS_MEMBER_EMAIL;
		accessToken = accessTokenAndRefreshToken.accessToken();
	}

	@Test
	@DisplayName("장소 투표방 조회에 성공한다.")
	void 장소투표방조회성공() throws Exception {
		// given
		// 방 생성
		final String roomId = "roomId";
		Room room = roomRepository.save(Room.builder()
			.id(roomId)
			.name("roomName")
			.memo("roomMemo")
			.build());

		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> CustomException.from(MEMBER_NOT_FOUND));

		memberRoomRepository.save(MemberRoom.builder()
			.member(member)
			.room(room)
			.build());

		// 장소 투표방 생성
		PlaceVoteRoom placeVoteRoom = placeVoteRoomRepository.save(new PlaceVoteRoom(room));
		List<PlaceVoteCandidate> placeVoteCandidates = List.of(
			new PlaceVoteCandidate(
				new PlaceCandidateInfo(
					"기존장소1",
					"서울1",
					"강남구1",
					"강남대로 1",
					37.123456,
					127.123456
				),
				placeVoteRoom
			),
			new PlaceVoteCandidate(
				new PlaceCandidateInfo(
					"기존장소2",
					"서울2",
					"강남구2",
					"강남대로 2",
					37.123,
					127.123
				),
				placeVoteRoom
			)
		);
		placeVoteCandidateRepository.saveAll(placeVoteCandidates);

		// when
		// 장소 투표방 조회
		ResultActions resultActions = mockMvc.perform(get("/api/place-vote-rooms/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + accessToken));

		// then
		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath("$.data.existence").value(true));

		for (int i = 0; i < placeVoteCandidates.size(); i++) {
			PlaceVoteCandidate pc = placeVoteCandidates.get(i);

			resultActions.andExpect(jsonPath("$.data.placeCandidates[" + i + "].id").exists());
			resultActions.andExpect(jsonPath("$.data.placeCandidates[" + i + "].name").value(pc.getName()));
			resultActions.andExpect(jsonPath("$.data.placeCandidates[" + i + "].siDo").value(pc.getSiDo()));
			resultActions.andExpect(jsonPath("$.data.placeCandidates[" + i + "].siGunGu").value(pc.getSiGunGu()));
			resultActions.andExpect(jsonPath("$.data.placeCandidates[" + i + "].roadNameAddress").value(pc.getRoadNameAddress()));
			resultActions.andExpect(jsonPath("$.data.placeCandidates[" + i + "].addressLat").value(pc.getAddressLatitude()));
			resultActions.andExpect(jsonPath("$.data.placeCandidates[" + i + "].addressLong").value(pc.getAddressLongitude()));
		}
	}

	@Test
	@DisplayName("저장된 장소 투표방이 없을 경우, false를 반환한다.")
	void 장소투표방조회실패() throws Exception {
		// given
		// 방 생성
		final String roomId = "roomId";
		roomRepository.save(Room.builder()
			.id(roomId)
			.name("roomName")
			.memo("roomMemo")
			.build());

		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> CustomException.from(MEMBER_NOT_FOUND));

		memberRoomRepository.save(MemberRoom.builder()
			.member(member)
			.room(roomRepository.findById(roomId).orElseThrow(() -> CustomException.from(ROOM_NOT_FOUND)))
			.build());

		// when
		ResultActions resultActions = mockMvc.perform(get("/api/place-vote-rooms/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + accessToken));

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath("$.data.existence").value(false));
		resultActions.andExpect(jsonPath("$.data.placeCandidates").doesNotExist());
	}

	@Test
	@DisplayName("해당 방의 회원이 아닐 경우 장소 투표방 조회에 실패한다.")
	void 장소투표방조회_해당방회원아닐경우_실패() throws Exception {
		// given
		// 방 생성
		final String roomId = "roomId";
		roomRepository.save(Room.builder()
			.id(roomId)
			.name("roomName")
			.memo("roomMemo")
			.build());

		// when
		ResultActions resultActions = mockMvc.perform(get("/api/place-vote-rooms/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + accessToken));

		// then
		resultActions.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("MR-003"));
	}
}
