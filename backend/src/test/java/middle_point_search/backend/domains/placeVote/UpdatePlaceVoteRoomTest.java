package middle_point_search.backend.domains.placeVote;

import static org.assertj.core.api.Assertions.*;
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
import org.springframework.test.web.servlet.ResultActions;

import middle_point_search.backend.common.BaseIntegrationTest;
import middle_point_search.backend.common.dto.AccessTokenAndRefreshToken;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.repository.MemberRepository;
import middle_point_search.backend.domains.memberRoom.domain.MemberRoom;
import middle_point_search.backend.domains.memberRoom.repository.MemberRoomRepository;
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteCandidate;
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteRoom;
import middle_point_search.backend.domains.placeVoteRoom.dto.dto.PlaceCandidateInfo;
import middle_point_search.backend.domains.placeVoteRoom.dto.request.UpdatePlaceVoteRoomRequest;
import middle_point_search.backend.domains.placeVoteRoom.repository.PlaceVoteCandidateRepository;
import middle_point_search.backend.domains.placeVoteRoom.repository.PlaceVoteRoomRepository;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.repository.RoomRepository;

@DisplayName("장소 투표 방 수정 테스트")
public class UpdatePlaceVoteRoomTest extends BaseIntegrationTest {

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
	@DisplayName("장소 투표 방 수정에 성공한다.")
	void 장소투표방수정성공() throws Exception {
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
			.orElseThrow(() -> new RuntimeException("Member not found"));

		// 방에 회원 저장
		memberRoomRepository.save(MemberRoom.builder()
			.room(room)
			.member(member)
			.build());

		// 장소 투표 방 생성
		PlaceVoteRoom placeVoteRoom = placeVoteRoomRepository.save(new PlaceVoteRoom(room));
		placeVoteCandidateRepository.saveAll(
			List.of(
				new PlaceVoteCandidate(
					new PlaceCandidateInfo(
						"기존장소",
						"서울",
						"강남구",
						"강남대로 123",
						37.123456,
						127.123456
					),
					placeVoteRoom
				)
			)
		);

		UpdatePlaceVoteRoomRequest request = new UpdatePlaceVoteRoomRequest(
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
		ResultActions resultActions = mockMvc.perform(put("/api/place-vote-rooms/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + accessToken)
			.contentType("application/json")
			.content(objectMapper.writeValueAsString(request)));

		// then
		resultActions.andExpect(status().isOk());

		// 장소 투표 방 수정 확인
		List<PlaceVoteCandidate> placeVoteCandidates = placeVoteCandidateRepository
			.findAllByPlaceVoteRoom(placeVoteRoom);

		for (int i = 0; i < request.placeCandidates().size(); i++) {
			PlaceVoteCandidate placeVoteCandidate = placeVoteCandidates.get(i);
			PlaceCandidateInfo placeCandidateInfo = request.placeCandidates().get(i);

			assertThat(placeVoteCandidate.getName()).isEqualTo(placeCandidateInfo.getName());
			assertThat(placeVoteCandidate.getSiDo()).isEqualTo(placeCandidateInfo.getSiDo());
			assertThat(placeVoteCandidate.getSiGunGu()).isEqualTo(placeCandidateInfo.getSiGunGu());
			assertThat(placeVoteCandidate.getRoadNameAddress()).isEqualTo(placeCandidateInfo.getRoadNameAddress());
			assertThat(placeVoteCandidate.getAddressLatitude()).isEqualTo(placeCandidateInfo.getAddressLat());
			assertThat(placeVoteCandidate.getAddressLongitude()).isEqualTo(placeCandidateInfo.getAddressLong());
		}
	}

	@Test
	@DisplayName("해당 방의 회원이 아닐 경우 장소 투표 방 수정에 실패한다.")
	void 장소투표방수정_해당방회원아닐경우_실패() throws Exception {
		// given
		// 방 생성
		final String roomId = "roomId";
		Room room = roomRepository.save(Room.builder()
			.id(roomId)
			.name("roomName")
			.memo("roomMemo")
			.build());

		UpdatePlaceVoteRoomRequest request = new UpdatePlaceVoteRoomRequest(
			List.of(
				new PlaceCandidateInfo(
					"장소1",
					"서울",
					"강남구",
					"강남대로 123",
					37.123456,
					127.123456
				)
			)
		);

		// when
		ResultActions resultActions = mockMvc.perform(put("/api/place-vote-rooms/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + accessToken)
			.contentType("application/json")
			.content(objectMapper.writeValueAsString(request)));

		// then
		resultActions.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("MR-003"));
	}

	@ParameterizedTest
	@DisplayName("장소 투표 방 수정 시 잘못된 요청 파라미터로 실패한다.")
	@MethodSource("provideInvalidUpdatePlaceVoteRoomRequest")
	void 장소투표방수정_잘못된요청파라미터_실패(UpdatePlaceVoteRoomRequest request) throws Exception {
		// given
		final String roomId = "roomId";
		Room room = roomRepository.save(Room.builder()
			.id(roomId)
			.name("roomName")
			.memo("roomMemo")
			.build());

		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new RuntimeException("Member not found"));

		memberRoomRepository.save(MemberRoom.builder()
			.room(room)
			.member(member)
			.build());

		placeVoteRoomRepository.save(new PlaceVoteRoom(room));

		// when
		ResultActions resultActions = mockMvc.perform(put("/api/place-vote-rooms/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + accessToken)
			.contentType("application/json")
			.content(objectMapper.writeValueAsString(request)));

		// then
		resultActions.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("C-202"));
	}

	// 장소 투표 방 생성 시 잘못된 요청 파라미터
	private static Stream<UpdatePlaceVoteRoomRequest> provideInvalidUpdatePlaceVoteRoomRequest() {
		return Stream.of(
			// 빈 리스트
			new UpdatePlaceVoteRoomRequest(List.of()),
			// 장소 후보 정보가 없는 경우
			new UpdatePlaceVoteRoomRequest(List.of(
				new PlaceCandidateInfo(
					"",
					"서울",
					"강남구",
					"강남대로 123",
					37.123456,
					127.123456
				)
			)),
			new UpdatePlaceVoteRoomRequest(List.of(
				new PlaceCandidateInfo(
					"강남역",
					"",
					"강남구",
					"강남대로 123",
					37.123456,
					127.123456
				)
			)),
			new UpdatePlaceVoteRoomRequest(List.of(
				new PlaceCandidateInfo(
					"강남역",
					"서울",
					"",
					"강남대로 123",
					37.123456,
					127.123456
				)
			)),
			new UpdatePlaceVoteRoomRequest(List.of(
				new PlaceCandidateInfo(
					"강남역",
					"서울",
					"강남구",
					"",
					37.123456,
					127.123456
				)
			)),
			new UpdatePlaceVoteRoomRequest(List.of(
				new PlaceCandidateInfo(
					"강남역",
					"서울",
					"강남구",
					"강남대로 123",
					null,
					127.123456
				)
			)),
			new UpdatePlaceVoteRoomRequest(List.of(
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
