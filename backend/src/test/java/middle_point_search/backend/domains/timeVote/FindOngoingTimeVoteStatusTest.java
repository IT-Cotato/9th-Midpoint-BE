package middle_point_search.backend.domains.timeVote;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
import middle_point_search.backend.domains.timeVoteRoom.domain.TimeVote;
import middle_point_search.backend.domains.timeVoteRoom.domain.TimeVoteRoom;
import middle_point_search.backend.domains.timeVoteRoom.repository.MeetingDateRepository;
import middle_point_search.backend.domains.timeVoteRoom.repository.TimeVoteRepository;
import middle_point_search.backend.domains.timeVoteRoom.repository.TimeVoteRoomRepository;

@DisplayName("진행중인 시간투표 상태 조회 테스트")
public class FindOngoingTimeVoteStatusTest extends BaseIntegrationTest {

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
	private TimeVoteRoomRepository timeVoteRoomRepository;
	@Autowired
	private TimeVoteRepository timeVoteRepository;
	@Autowired
	private MeetingDateRepository meetingDateRepository;

	@BeforeEach
	void setUp() throws Exception {
		AccessTokenAndRefreshToken accessTokenAndRefreshToken = signupAndLoginMember(false);
		signupAndLoginMember(true);

		memberEmail = NO_ADDRESS_MEMBER_EMAIL;
		memberAccessToken = accessTokenAndRefreshToken.accessToken();

		friendEmail = ADDRESS_MEMBER_EMAIL;
	}

	@Test
	@DisplayName("진행중인 시간투표 상태 조회에 성공한다.")
	void 진행중인시간투표상태조회성공() throws Exception {
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

		// 회원 방 생성
		MemberRoom memberRoom = MemberRoom.builder()
			.room(room)
			.member(member)
			.build();
		memberRoomRepository.save(memberRoom);

		// 만날 날짜
		LocalDate localDate = LocalDate.now();
		LocalDateTime startTime = localDate.atTime(10, 0);
		LocalDateTime endTime = localDate.atTime(12, 0);

		// 시간투표 방 생성
		TimeVoteRoom timeVoteRoom = new TimeVoteRoom(room);
		MeetingDate meetingDate = new MeetingDate(timeVoteRoom, localDate);
		meetingDateRepository.save(meetingDate);
		timeVoteRoomRepository.save(timeVoteRoom);

		// 시간투표 생성
		timeVoteRepository.save(new TimeVote(
			timeVoteRoom,
			meetingDate,
			member,
			startTime,
			endTime));

		// when
		ResultActions resultActions = mockMvc.perform(get("/api/time-votes/voted/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + memberAccessToken)
			.param("roomId", roomId));

		// then
		String formattedStartTime = startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		String formattedEndTime = endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.myVotesExistence").value(true))
			.andExpect(jsonPath("$.data.otherVotesExistence").value(false))
			.andExpect(jsonPath("$.data.myVotes[0].memberAvailableStartTime").value(formattedStartTime))
			.andExpect(jsonPath("$.data.myVotes[0].memberAvailableEndTime").value(formattedEndTime));
	}

	@Test
	@DisplayName("친구들의 투표가 있는 경우 진행중인 시간투표 상태 조회에 성공한다.")
	void 친구들의투표가있는경우진행중인시간투표상태조회성공() throws Exception {
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
		Member friend = memberRepository.findByEmail(friendEmail).get();

		// 회원을 방에 등록
		MemberRoom memberRoom = MemberRoom.builder()
			.room(room)
			.member(member)
			.build();
		memberRoomRepository.save(memberRoom);

		// 친구를 방에 등록
		MemberRoom friendRoom = MemberRoom.builder()
			.room(room)
			.member(friend)
			.build();
		memberRoomRepository.save(friendRoom);

		// 만날 날짜
		LocalDate localDate = LocalDate.now();
		LocalDateTime startTime = localDate.atTime(10, 0);
		LocalDateTime endTime = localDate.atTime(12, 0);

		// 시간투표 방 생성
		TimeVoteRoom timeVoteRoom = new TimeVoteRoom(room);
		MeetingDate meetingDate = new MeetingDate(timeVoteRoom, localDate);
		meetingDateRepository.save(meetingDate);
		timeVoteRoomRepository.save(timeVoteRoom);

		// 친구 시간투표 생성
		timeVoteRepository.save(new TimeVote(
			timeVoteRoom,
			meetingDate,
			friend,
			startTime,
			endTime));

		// when
		ResultActions resultActions = mockMvc.perform(get("/api/time-votes/voted/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + memberAccessToken)
			.param("roomId", roomId));

		// then
		String formattedMeetingDate = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String formattedStartTime = startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		String formattedEndTime = endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.myVotesExistence").value(false))
			.andExpect(jsonPath("$.data.otherVotesExistence").value(true))
			.andExpect(jsonPath("$.data.otherVotes[0].date").value(formattedMeetingDate))
			.andExpect(jsonPath("$.data.otherVotes[0].timeVotes[0].memberName").value(friend.getEmail())) // 이메일 리턴
			.andExpect(jsonPath("$.data.otherVotes[0].timeVotes[0].dateTime.memberAvailableStartTime").value(formattedStartTime))
			.andExpect(jsonPath("$.data.otherVotes[0].timeVotes[0].dateTime.memberAvailableEndTime").value(formattedEndTime));
	}

	@Test
	@DisplayName("아무도 투표를 하지 않은 경우 진행중인 시간투표 상태 조회에 성공한다.")
	void 아무도투표를하지않은경우진행중인시간투표상태조회성공() throws Exception {
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

		// 회원 방 생성
		MemberRoom memberRoom = MemberRoom.builder()
			.room(room)
			.member(member)
			.build();
		memberRoomRepository.save(memberRoom);

		// 만날 날짜
		LocalDate localDate = LocalDate.now();

		// 시간투표 방 생성
		TimeVoteRoom timeVoteRoom = new TimeVoteRoom(room);
		MeetingDate meetingDate = new MeetingDate(timeVoteRoom, localDate);
		meetingDateRepository.save(meetingDate);
		timeVoteRoomRepository.save(timeVoteRoom);

		// when
		ResultActions resultActions = mockMvc.perform(get("/api/time-votes/voted/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + memberAccessToken)
			.param("roomId", roomId));

		// then
		String formattedMeetingDate = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.myVotesExistence").value(false))
			.andExpect(jsonPath("$.data.otherVotesExistence").value(false));
	}

	@Test
	@DisplayName("해당 방에 회원이 아니면 진행중인 시간투표 상태 조회에 실패한다.")
	void 해당방에회원이아니면진행중인시간투표상태조회실패() throws Exception {
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

		// 회원을 방에 저장 x

		// 만날 날짜
		LocalDate localDate = LocalDate.now();
		LocalDateTime startTime = localDate.atTime(10, 0);
		LocalDateTime endTime = localDate.atTime(12, 0);

		// 시간투표 방 생성
		TimeVoteRoom timeVoteRoom = new TimeVoteRoom(room);
		MeetingDate meetingDate = new MeetingDate(timeVoteRoom, localDate);
		meetingDateRepository.save(meetingDate);
		timeVoteRoomRepository.save(timeVoteRoom);

		// 시간투표 생성
		timeVoteRepository.save(new TimeVote(
			timeVoteRoom,

			meetingDate,
			member,
			startTime,
			endTime));

		// when
		ResultActions resultActions = mockMvc.perform(get("/api/time-votes/voted/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + memberAccessToken)
			.param("roomId", roomId));

		// then
		resultActions
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("MR-003"));
	}

	@Test
	@DisplayName("생성된 투표방이 없으면 진행중인 시간투표 상태 조회에 실패한다.")
	void 생성된투표방이없으면진행중인시간투표상태조회실패() throws Exception {
		// given
		// 방 생성 x

		// 회원 id 조회
		Member member = memberRepository.findByEmail(memberEmail).get();

		// 회원 방 생성
		String roomId = "roomId";
		Room room = Room.builder()
			.id(roomId)
			.name("roomName")
			.memo("roomMemo")
			.build();
		roomRepository.save(room);

		memberRoomRepository.save(MemberRoom.builder()
			.room(room)
			.member(member)
			.build());

		// 시간투표 방 생성 x
		// 시간투표 생성 x

		// when
		ResultActions resultActions = mockMvc.perform(get("/api/time-votes/voted/rooms/{roomId}", "roomId")
			.header("Authorization", "Bearer " + memberAccessToken)
			.param("roomId", "roomId"));

		// then
		resultActions
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("V-202"));
	}
}
