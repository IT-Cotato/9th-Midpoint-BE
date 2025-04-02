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

@DisplayName("시간 투표 결과 조회 테스트")
public class FindTimeVoteResultTest extends BaseIntegrationTest {

	private String memberAccessToken;
	private String memberEmail;
	private String friendAccessToken;
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
		memberAccessToken = signupAndLoginMember(false).accessToken();
		memberEmail = NO_ADDRESS_MEMBER_EMAIL;

		friendAccessToken = signupAndLoginMember(true).accessToken();
		friendEmail = ADDRESS_MEMBER_EMAIL;
	}

	// 시간 투표 결과 조회에 성공한다.
	@Test
	@DisplayName("시간 투표 결과 조회에 성공한다.")
	void 시간투표결과조회성공() throws Exception {
		// given
		// 방 생성
		String roomId = "roomId";
		Room room = Room.builder()
			.id(roomId)
			.name("roomName")
			.memo("roomMemo")
			.build();
		roomRepository.save(room);

		// 회원 및 친구 조회
		Member member = memberRepository.findByEmail(memberEmail).get();
		Member friend = memberRepository.findByEmail(friendEmail).get();

		// 방에 회원 추가
		memberRoomRepository.save(MemberRoom.builder()
			.member(member)
			.room(room)
			.build());
		memberRoomRepository.save(MemberRoom.builder()
			.member(friend)
			.room(room)
			.build());

		// 만나는 날짜 및 투표 시간
		LocalDate meetingDate1 = LocalDate.now();
		LocalDate meetingDate2 = LocalDate.now().minusDays(1);

		// 만날 수 있는 날짜는 2개이며, 멤버와 친구는 각 날짜 같은 시간에 투표했다 가정
		// 시간 투표 방 생성
		TimeVoteRoom timeVoteRoom = new TimeVoteRoom(room);
		MeetingDate meetingDateEntity1 = new MeetingDate(timeVoteRoom, meetingDate1);
		meetingDateRepository.save(meetingDateEntity1);
		MeetingDate meetingDateEntity2 = new MeetingDate(timeVoteRoom, meetingDate2);
		meetingDateRepository.save(meetingDateEntity2);
		timeVoteRoomRepository.save(timeVoteRoom);

		// 시간 투표1 생성
		LocalDateTime availableStartTime1 = meetingDate1.atTime(10, 0);
		LocalDateTime availableEndTime1 = meetingDate1.atTime(12, 0);
		timeVoteRepository.save(new TimeVote(
			timeVoteRoom,
			meetingDateEntity1,
			member,
			availableStartTime1,
			availableEndTime1
		));
		timeVoteRepository.save(new TimeVote(
			timeVoteRoom,
			meetingDateEntity1,
			friend,
			availableStartTime1,
			availableEndTime1
		));

		// 시간 투표2 생성
		LocalDateTime availableStartTime2 = meetingDate2.atTime(13, 0);
		LocalDateTime availableEndTime2 = meetingDate2.atTime(15, 0);
		timeVoteRepository.save(new TimeVote(
			timeVoteRoom,
			meetingDateEntity2,
			member,
			availableStartTime2,
			availableEndTime2
		));
		timeVoteRepository.save(new TimeVote(
			timeVoteRoom,
			meetingDateEntity2,
			friend,
			availableStartTime2,
			availableEndTime2
		));

		// when
		ResultActions resultActions = mockMvc.perform(
			get("/api/time-votes/result/rooms/{roomId}", roomId)
				.header("Authorization", "Bearer " + memberAccessToken)
				.param("roomId", roomId)
		);

		// then
		resultActions
			.andExpect(status().isOk());

		// 결과 확인
		String formattedMeetingDate1 = meetingDate1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String formattedStartTime1 = availableStartTime1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		String formattedEndTime1 = availableEndTime1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

		String formattedMeetingDate2 = meetingDate2.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String formattedStartTime2 = availableStartTime2.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		String formattedEndTime2 = availableEndTime2.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

		// 날짜 1
		resultActions
			.andExpect(jsonPath("$.data.totalMemberNum").value(2))
			.andExpect(jsonPath("$.data.result." + formattedMeetingDate1 + "[0].memberName").value(member.getEmail()))
			.andExpect(
				jsonPath("$.data.result." + formattedMeetingDate1 + "[0].dateTime.memberAvailableStartTime").value(
					formattedStartTime1))
			.andExpect(jsonPath("$.data.result." + formattedMeetingDate1 + "[0].dateTime.memberAvailableEndTime").value(
				formattedEndTime1));
		resultActions
			.andExpect(jsonPath("$.data.result." + formattedMeetingDate1 + "[1].memberName").value(friend.getEmail()))
			.andExpect(
				jsonPath("$.data.result." + formattedMeetingDate1 + "[1].dateTime.memberAvailableStartTime").value(
					formattedStartTime1))
			.andExpect(jsonPath("$.data.result." + formattedMeetingDate1 + "[1].dateTime.memberAvailableEndTime").value(
				formattedEndTime1));
		// 날짜 2
		resultActions
			.andExpect(jsonPath("$.data.result." + formattedMeetingDate2 + "[0].memberName").value(member.getEmail()))
			.andExpect(
				jsonPath("$.data.result." + formattedMeetingDate2 + "[0].dateTime.memberAvailableStartTime").value(
					formattedStartTime2))
			.andExpect(jsonPath("$.data.result." + formattedMeetingDate2 + "[0].dateTime.memberAvailableEndTime").value(
				formattedEndTime2));
		resultActions
			.andExpect(jsonPath("$.data.result." + formattedMeetingDate2 + "[1].memberName").value(friend.getEmail()))
			.andExpect(
				jsonPath("$.data.result." + formattedMeetingDate2 + "[1].dateTime.memberAvailableStartTime").value(
					formattedStartTime2))
			.andExpect(jsonPath("$.data.result." + formattedMeetingDate2 + "[1].dateTime.memberAvailableEndTime").value(
				formattedEndTime2));
	}

	@Test
	@DisplayName("투표한 회원이 없는 경우, 날짜에 해당하는 투표 결과가 빈 리스트로 응답된다.")
	void 투표한회원이없는경우날짜에해당하는투표결과가빈리스트로응답된다() throws Exception {
		// given
		// 방 생성
		String roomId = "roomId";
		Room room = Room.builder()
			.id(roomId)
			.name("roomName")
			.memo("roomMemo")
			.build();
		roomRepository.save(room);

		// 회원 및 친구 조회
		Member member = memberRepository.findByEmail(memberEmail).get();

		// 방에 회원 추가
		memberRoomRepository.save(MemberRoom.builder()
			.member(member)
			.room(room)
			.build());

		// 만나는 날짜 및 투표 시간
		LocalDate meetingDate = LocalDate.now();

		// 만날 수 있는 날짜는 2개이며, 멤버와 친구는 각 날짜 같은 시간에 투표했다 가정
		// 시간 투표 방 생성
		TimeVoteRoom timeVoteRoom = new TimeVoteRoom(room);
		MeetingDate meetingDateEntity1 = new MeetingDate(timeVoteRoom, meetingDate);
		meetingDateRepository.save(meetingDateEntity1);
		timeVoteRoomRepository.save(timeVoteRoom);

		// when
		ResultActions resultActions = mockMvc.perform(
			get("/api/time-votes/result/rooms/{roomId}", roomId)
				.header("Authorization", "Bearer " + memberAccessToken)
				.param("roomId", roomId)
		);

		// then
		String formattedMeetingDate = meetingDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.totalMemberNum").value(0))
			.andExpect(jsonPath("$.data.result." + formattedMeetingDate).isEmpty());
	}

	@Test
	@DisplayName("해당 방의 회원이 아닌 경우 시간 투표 결과 조회에 실패한다.")
	void 해당방의회원이아닌경우시간투표결과조회에실패한다() throws Exception {
		// given
		// 방 생성
		String roomId = "roomId";
		Room room = Room.builder()
			.id(roomId)
			.name("roomName")
			.memo("roomMemo")
			.build();
		roomRepository.save(room);

		// 만나는 날짜 및 투표 시간
		LocalDate meetingDate1 = LocalDate.now();

		// 만날 수 있는 날짜는 2개이며, 멤버와 친구는 각 날짜 같은 시간에 투표했다 가정
		// 시간 투표 방 생성
		TimeVoteRoom timeVoteRoom = new TimeVoteRoom(room);
		MeetingDate meetingDateEntity1 = new MeetingDate(timeVoteRoom, meetingDate1);
		meetingDateRepository.save(meetingDateEntity1);
		timeVoteRoomRepository.save(timeVoteRoom);

		// when
		ResultActions resultActions = mockMvc.perform(
			get("/api/time-votes/result/rooms/{roomId}", roomId)
				.header("Authorization", "Bearer " + memberAccessToken)
				.param("roomId", roomId)
		);

		// then
		resultActions
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("MR-003"));
	}

	@Test
	@DisplayName("생성된 투표방이 없는 경우 시간 투표 결과 조회에 실패한다.")
	void 생성된투표방이없는경우시간투표결과조회에실패한다() throws Exception {
		// given
		// 방 생성
		String roomId = "roomId";
		Room room = Room.builder()
			.id(roomId)
			.name("roomName")
			.memo("roomMemo")
			.build();
		roomRepository.save(room);

		// 회원 및 친구 조회
		Member member = memberRepository.findByEmail(memberEmail).get();

		// 방에 회원 추가
		memberRoomRepository.save(MemberRoom.builder()
			.member(member)
			.room(room)
			.build());

		// when
		ResultActions resultActions = mockMvc.perform(
			get("/api/time-votes/result/rooms/{roomId}", roomId)
				.header("Authorization", "Bearer " + memberAccessToken)
				.param("roomId", roomId)
		);

		// then
		resultActions
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("V-202"));
	}
}