package middle_point_search.backend.domains.timeVote;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.ArrayList;
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
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.repository.RoomRepository;
import middle_point_search.backend.domains.timeVoteRoom.domain.MeetingDate;
import middle_point_search.backend.domains.timeVoteRoom.domain.TimeVoteRoom;
import middle_point_search.backend.domains.timeVoteRoom.dto.request.CreateTimeVoteRoomRequest;
import middle_point_search.backend.domains.timeVoteRoom.repository.MeetingDateRepository;
import middle_point_search.backend.domains.timeVoteRoom.repository.TimeVoteRoomRepository;

@DisplayName("시간 투표 방 생성 테스트")
public class CreateTimeVoteRoomTest extends BaseIntegrationTest {

	private String accessToken;
	private String memberEmail;

	@Autowired
	private MemberRoomRepository memberRoomRepository;
	@Autowired
	private RoomRepository roomRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private TimeVoteRoomRepository timeVoteRoomRepository;
	@Autowired
	private MeetingDateRepository meetingDateRepository;

	@BeforeEach
	void setUp() throws Exception {
		AccessTokenAndRefreshToken accessTokenAndRefreshToken = signupAndLoginMember(false);

		memberEmail = NO_ADDRESS_MEMBER_EMAIL;
		accessToken = accessTokenAndRefreshToken.accessToken();
	}

	@Test
	@DisplayName("시간 투표 방 생성에 성공한다.")
	void 시간투표방생성성공() throws Exception {
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

		// 시간 투표 방 생성 Request 생성

		LocalDate localDate1 = LocalDate.now();
		CreateTimeVoteRoomRequest createTimeVoteRoomRequest = new CreateTimeVoteRoomRequest(
			List.of(
				localDate1
			)
		);
		// when
		// 시간 투표 방 생성
		ResultActions resultActions = mockMvc.perform(post("/api/time-vote-rooms/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(createTimeVoteRoomRequest))
		);

		// then
		resultActions.andExpect(status().isOk());
		// 시간 투표 방 생성 확인
		TimeVoteRoom timeVoteRoom = timeVoteRoomRepository.findByRoom_Id(roomId)
			.orElseThrow(() -> new IllegalArgumentException("시간 투표 방이 존재하지 않습니다."));
		List<MeetingDate> meetingDates = meetingDateRepository.findAllByTimeVoteRoom(timeVoteRoom);
		MeetingDate meetingDate = meetingDates.get(0);
		assertThat(meetingDate.getDate()).isEqualTo(localDate1);
	}

	@Test
	@DisplayName("해당 방의 회원이 아닌 회원이 시간 투표 방을 생성하면 실패한다.")
	void 해당방의회원이아닌회원이시간투표방을생성하면실패() throws Exception {
		// given
		// 방 생성
		String roomId = "roomId";
		Room room = Room.builder()
			.id(roomId)
			.name("roomName")
			.memo("roomMemo")
			.build();
		roomRepository.save(room);

		// when
		// 방에 회원 저장하지 않음

		// 시간 투표 방 생성 Request 생성
		LocalDate localDate1 = LocalDate.now();
		CreateTimeVoteRoomRequest createTimeVoteRoomRequest = new CreateTimeVoteRoomRequest(
			List.of(
				localDate1
			)
		);
		// when
		// 시간 투표 방 생성
		ResultActions resultActions = mockMvc.perform(post("/api/time-vote-rooms/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(createTimeVoteRoomRequest))
		);

		// then
		resultActions.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("MR-003"));
	}

	@Test
	@DisplayName("이미 투표방이 존재하는 방에 시간 투표 방을 생성하면 실패한다.")
	void 이미투표방이존재하는방에시간투표방을생성하면실패() throws Exception {
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

		// 시간 투표 방 생성 Request 생성
		LocalDate localDate1 = LocalDate.now();
		CreateTimeVoteRoomRequest createTimeVoteRoomRequest = new CreateTimeVoteRoomRequest(
			List.of(
				localDate1
			)
		);

		// 시간 투표 방 생성
		timeVoteRoomRepository.save(new TimeVoteRoom(room));

		// when
		// 시간 투표 방 생성
		ResultActions resultActions = mockMvc.perform(post("/api/time-vote-rooms/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(createTimeVoteRoomRequest))
		);

		// then
		resultActions.andExpect(status().isConflict())
			.andExpect(jsonPath("$.code").value("V-302"));
	}

	@ParameterizedTest
	@DisplayName("시간 투표 방 생성 시 요청 파라미터가 잘못되면 실패한다.")
	@MethodSource("provideInvalidCreateTimeVoteRoomRequest")
	void 시간투표방생성시요청파라미터가잘못되면실패(CreateTimeVoteRoomRequest createTimeVoteRoomRequest) throws Exception {
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

		// when
		// 시간 투표 방 생성
		ResultActions resultActions = mockMvc.perform(post("/api/time-vote-rooms/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(createTimeVoteRoomRequest))
		);

		// then
		resultActions.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("C-202"));
	}

	// 시간 투표 방 생성 시 잘못된 요청 파라미터
	private static Stream<CreateTimeVoteRoomRequest> provideInvalidCreateTimeVoteRoomRequest() {
		return Stream.of(
			new CreateTimeVoteRoomRequest(List.of()),  // 빈 리스트 (NotEmpty 위반)
			new CreateTimeVoteRoomRequest(new ArrayList<>())  // dates가 빔
		);
	}
}
