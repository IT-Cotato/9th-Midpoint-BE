package middle_point_search.backend.domains.timeVote;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
import middle_point_search.backend.domains.timeVoteRoom.domain.TimeVote;
import middle_point_search.backend.domains.timeVoteRoom.domain.TimeVoteRoom;
import middle_point_search.backend.domains.timeVoteRoom.dto.dto.TimeRange;
import middle_point_search.backend.domains.timeVoteRoom.dto.request.UpdateTimeVoteRequest;
import middle_point_search.backend.domains.timeVoteRoom.repository.MeetingDateRepository;
import middle_point_search.backend.domains.timeVoteRoom.repository.TimeVoteRepository;
import middle_point_search.backend.domains.timeVoteRoom.repository.TimeVoteRoomRepository;

@DisplayName("시간투표 수정 테스트")
public class UpdateTimeVoteTest extends BaseIntegrationTest {

	private String memberAccessToken;
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
	private TimeVoteRepository timeVoteRepository;
	@Autowired
	private MeetingDateRepository meetingDateRepository;

	@BeforeEach
	void setUp() throws Exception {
		AccessTokenAndRefreshToken accessTokenAndRefreshToken = signupAndLoginMember(false);

		memberEmail = NO_ADDRESS_MEMBER_EMAIL;
		memberAccessToken = accessTokenAndRefreshToken.accessToken();
	}

	// 시간투표 수정에 성공한다.
	@Test
	@DisplayName("시간투표 수정에 성공한다.")
	void 시간투표수정성공() throws Exception {
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
			.orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

		// 방에 회원 저장
		memberRoomRepository.save(MemberRoom.builder()
			.member(member)
			.room(room)
			.build()
		);

		// 시간투표방 생성
		LocalDate localDate = LocalDate.now();
		LocalDateTime startTime = localDate.atTime(10, 0);
		LocalDateTime endTime = localDate.atTime(11, 0);

		TimeVoteRoom timeVoteRoom = new TimeVoteRoom(room);
		MeetingDate meetingDate = new MeetingDate(timeVoteRoom, localDate); // 만나는 날
		meetingDateRepository.save(meetingDate);
		timeVoteRoomRepository.save(timeVoteRoom);

		// 시간투표 생성
		timeVoteRepository.save(new TimeVote(timeVoteRoom, meetingDate, member, startTime, endTime));

		// 시간투표 수정
		LocalDateTime newStartTime = localDate.atTime(11, 0);
		LocalDateTime newEndTime = localDate.atTime(12, 0);
		TimeRange timeRange = new TimeRange(newStartTime, newEndTime);
		UpdateTimeVoteRequest updateTimeVoteRequest = new UpdateTimeVoteRequest(List.of(
			timeRange
		));

		// when
		ResultActions resultActions = mockMvc.perform(put("/api/time-votes/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + memberAccessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(updateTimeVoteRequest)));

		// then
		resultActions.andExpect(status().isOk());
		// 시간투표 수정 확인
		timeVoteRepository.findAllByTimeVoteRoomAndMeetingDate(timeVoteRoom, meetingDate)
			.forEach(timeVote -> {
				assertThat(timeVote.getMemberAvailableStartTime()).isEqualTo(newStartTime);
				assertThat(timeVote.getMemberAvailableEndTime()).isEqualTo(newEndTime);
			});
	}

	@Test
	@DisplayName("시간투표가 없으면 예외를 발생시킨다.")
	void 시간투표가없으면예외발생() throws Exception {
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
			.orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

		// 방에 회원 저장
		memberRoomRepository.save(MemberRoom.builder()
			.member(member)
			.room(room)
			.build()
		);

		// 시간투표방 생성
		LocalDate localDate = LocalDate.now();
		LocalDateTime startTime = localDate.atTime(10, 0);
		LocalDateTime endTime = localDate.atTime(11, 0);

		TimeVoteRoom timeVoteRoom = new TimeVoteRoom(room);
		MeetingDate meetingDate = new MeetingDate(timeVoteRoom, localDate); // 만나는 날
		meetingDateRepository.save(meetingDate);
		timeVoteRoomRepository.save(timeVoteRoom);

		// when
		UpdateTimeVoteRequest updateTimeVoteRequest = new UpdateTimeVoteRequest(List.of(
			new TimeRange(startTime, endTime)
		));
		ResultActions resultActions = mockMvc.perform(put("/api/time-votes/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + memberAccessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(updateTimeVoteRequest)));

		// then
		resultActions
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("V-201"));
	}

	@Test
	@DisplayName("해당 방의 회원이 아닌 회원이 시간투표를 수정하면 실패한다.")
	void 해당방의회원이아닌회원이시간투표를수정하면실패() throws Exception {
		// given
		// 방 생성
		String roomId = "roomId";
		Room room = Room.builder()
			.id(roomId)
			.name("roomName")
			.memo("roomMemo")
			.build();
		roomRepository.save(room);

		// 방에 회원 저장하지 않음

		// 시간투표방 생성
		LocalDate localDate = LocalDate.now();

		TimeVoteRoom timeVoteRoom = new TimeVoteRoom(room);
		MeetingDate meetingDate = new MeetingDate(timeVoteRoom, localDate); // 만나는 날
		meetingDateRepository.save(meetingDate);
		timeVoteRoomRepository.save(timeVoteRoom);

		// 시간투표 수정
		LocalDateTime newStartTime = localDate.atTime(11, 0);
		LocalDateTime newEndTime = localDate.atTime(12, 0);
		TimeRange timeRange = new TimeRange(newStartTime, newEndTime);
		UpdateTimeVoteRequest updateTimeVoteRequest = new UpdateTimeVoteRequest(List.of(
			timeRange
		));

		// when
		ResultActions resultActions = mockMvc.perform(put("/api/time-votes/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + memberAccessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(updateTimeVoteRequest)));

		// then
		resultActions.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("MR-003"));
	}

	@Test
	@DisplayName("생성된 시간 투표방이 없으면 실패한다.")
	void 생성된시간투표방이없으면실패() throws Exception {
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
			.orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

		// 방에 회원 저장
		memberRoomRepository.save(MemberRoom.builder()
			.member(member)
			.room(room)
			.build()
		);

		// when
		UpdateTimeVoteRequest updateTimeVoteRequest = new UpdateTimeVoteRequest(List.of(
			new TimeRange(LocalDateTime.now(), LocalDateTime.now())
		));
		ResultActions resultActions = mockMvc.perform(put("/api/time-votes/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + memberAccessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(updateTimeVoteRequest)));

		// then
		resultActions
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("V-202"));
	}

	@Test
	@DisplayName("시간투표 후보가 아니면 실패한다.")
	void 시간투표후보가아니면실패() throws Exception {
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
			.orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

		// 방에 회원 저장
		memberRoomRepository.save(MemberRoom.builder()
			.member(member)
			.room(room)
			.build()
		);

		// 시간투표방 생성
		LocalDate localDate = LocalDate.of(2021, 1, 1);
		LocalDateTime startTime = localDate.atTime(10, 0);
		LocalDateTime endTime = localDate.atTime(11, 0);

		TimeVoteRoom timeVoteRoom = new TimeVoteRoom(room);
		MeetingDate meetingDate = new MeetingDate(timeVoteRoom, localDate); // 만나는 날
		meetingDateRepository.save(meetingDate);
		timeVoteRoomRepository.save(timeVoteRoom);

		// 시간투표 생성
		timeVoteRepository.save(new TimeVote(timeVoteRoom, meetingDate, member, startTime, endTime));

		// when
		UpdateTimeVoteRequest updateTimeVoteRequest = new UpdateTimeVoteRequest(List.of(
			new TimeRange(LocalDateTime.of(2021, 1, 2, 10, 0), LocalDateTime.of(2021, 1, 2, 11, 0))
		));
		ResultActions resultActions = mockMvc.perform(put("/api/time-votes/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + memberAccessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(updateTimeVoteRequest)));

		// then
		resultActions
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("V-101"));
	}
}
