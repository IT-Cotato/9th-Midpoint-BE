package middle_point_search.backend.domains.timeVote;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

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
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.repository.RoomRepository;
import middle_point_search.backend.domains.timeVoteRoom.domain.MeetingDate;
import middle_point_search.backend.domains.timeVoteRoom.domain.TimeVoteRoom;
import middle_point_search.backend.domains.timeVoteRoom.repository.TimeVoteRoomRepository;

@DisplayName("시간 투표 방 조회 테스트")
public class FindTimeVoteRoomTest extends BaseIntegrationTest {

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

	@BeforeEach
	void setUp() throws Exception {
		AccessTokenAndRefreshToken accessTokenAndRefreshToken = signupAndLoginMember(false);

		memberEmail = NO_ADDRESS_MEMBER_EMAIL;
		accessToken = accessTokenAndRefreshToken.accessToken();
	}

	// 시간 투표방 조회에 성공한다.
	@Test
	@DisplayName("시간 투표방 조회에 성공한다.")
	void 시간투표방조회성공() throws Exception {
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
		Member member = memberRepository.findByEmail(memberEmail).get();

		memberRoomRepository.save(MemberRoom.builder()
			.member(member)
			.room(room)
			.build());

		// 시간 투표방 생성
		LocalDate localDate = LocalDate.now();

		TimeVoteRoom timeVoteRoom = new TimeVoteRoom(room);
		timeVoteRoom.addMeetingDate(new MeetingDate(timeVoteRoom, localDate));
		timeVoteRoomRepository.save(timeVoteRoom);

		// when
		ResultActions resultActions = mockMvc.perform(get("/api/time-vote-rooms/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.existence").value(true))
			.andExpect(jsonPath("$.data.dates[0]").value(localDate.toString()));
	}

	@Test
	@DisplayName("시간 투표방에 여러 날짜가 있으면 모든 날짜를 조회한다.")
	void 시간투표방_여러_날짜_조회() throws Exception {
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
		Member member = memberRepository.findByEmail(memberEmail).get();

		memberRoomRepository.save(MemberRoom.builder()
			.member(member)
			.room(room)
			.build());

		// 시간 투표방 생성
		LocalDate localDate1 = LocalDate.now();
		LocalDate localDate2 = LocalDate.now().plusDays(1);

		TimeVoteRoom timeVoteRoom = new TimeVoteRoom(room);
		timeVoteRoom.addMeetingDate(new MeetingDate(timeVoteRoom, localDate1));
		timeVoteRoom.addMeetingDate(new MeetingDate(timeVoteRoom, localDate2));
		timeVoteRoomRepository.save(timeVoteRoom);

		// when
		ResultActions resultActions = mockMvc.perform(get("/api/time-vote-rooms/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.existence").value(true))
			.andExpect(jsonPath("$.data.dates[0]").value(localDate1.toString()))
			.andExpect(jsonPath("$.data.dates[1]").value(localDate2.toString()));
	}

	@Test
	@DisplayName("시간 투표방이 없으면 존재하지 않는다고 응답한다.")
	void 시간투표방이_없으면_존재하지_않는다고_응답() throws Exception {
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
		Member member = memberRepository.findByEmail(memberEmail).get();

		memberRoomRepository.save(MemberRoom.builder()
			.member(member)
			.room(room)
			.build());

		// when
		ResultActions resultActions = mockMvc.perform(get("/api/time-vote-rooms/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.existence").value(false))
			.andExpect(jsonPath("$.data.dates").isEmpty()); // 빈 배열이나 null
	}

	@Test
	@DisplayName("해당 방의 회원이 아닌 회원이 시간 투표 방을 조회하면 실패한다.")
	void 해당방의회원이아닌회원이시간투표방을조회하면실패() throws Exception {
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

		// 시간투표방 생성
		timeVoteRoomRepository.save(new TimeVoteRoom(room));

		// when
		// 시간 투표 방 조회
		ResultActions resultActions = mockMvc.perform(get("/api/time-vote-rooms/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON));

		// then
		resultActions
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("MR-003"));
	}
}
