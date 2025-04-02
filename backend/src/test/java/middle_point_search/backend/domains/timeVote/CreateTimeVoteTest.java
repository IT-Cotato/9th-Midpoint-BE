package middle_point_search.backend.domains.timeVote;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.repository.RoomRepository;
import middle_point_search.backend.domains.timeVoteRoom.domain.MeetingDate;
import middle_point_search.backend.domains.timeVoteRoom.domain.TimeVote;
import middle_point_search.backend.domains.timeVoteRoom.domain.TimeVoteRoom;
import middle_point_search.backend.domains.timeVoteRoom.dto.dto.TimeRange;
import middle_point_search.backend.domains.timeVoteRoom.dto.request.CreateTimeVoteRequest;
import middle_point_search.backend.domains.timeVoteRoom.repository.MeetingDateRepository;
import middle_point_search.backend.domains.timeVoteRoom.repository.TimeVoteRepository;
import middle_point_search.backend.domains.timeVoteRoom.repository.TimeVoteRoomRepository;

@DisplayName("시간 투표하기 테스트")
public class CreateTimeVoteTest extends BaseIntegrationTest {

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
	EntityManager entityManager;
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

	@Test
	@DisplayName("시간 투표하기에 성공한다.")
	void 시간투표하기성공() throws Exception {
		// given
		// 방 생성
		final String roomId = "roomId";
		Room room = Room.builder()
			.id(roomId)
			.name("roomName")
			.memo("roomMemo")
			.build();
		roomRepository.save(room);

		Member member = memberRepository.findByEmail(memberEmail).get();

		memberRoomRepository.save(
			MemberRoom.builder()
				.member(member)
				.room(room)
				.build());

		// 시간 투표방 생성
		final LocalDate localDate = LocalDate.now(); // 만나는 날짜
		TimeVoteRoom timeVoteRoom = new TimeVoteRoom(room);
		meetingDateRepository.save(new MeetingDate(timeVoteRoom, localDate));
		timeVoteRoomRepository.save(timeVoteRoom);

		// 투표할 시간
		final LocalDateTime startTime = localDate.atTime(3, 0); // 만나는 날짜 중 시작 시간
		final LocalDateTime endTime = localDate.atTime(4, 0); // 만나는 날짜 중 끝 시간

		// 시간 투표하기
		CreateTimeVoteRequest request = new CreateTimeVoteRequest(
			List.of(
				new TimeRange(startTime, endTime)
			)
		);

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/time-votes/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + memberAccessToken)
			.param("roomId", roomId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request))
		);

		// then
		resultActions
			.andExpect(status().isOk());

		// 시간 투표 확인
		entityManager.detach(timeVoteRoom); // 영속성 컨텍스트 초기화

		timeVoteRoom = timeVoteRoomRepository.findByRoom_Id(roomId)
			.orElseThrow(() -> new IllegalArgumentException("시간 투표 방이 존재하지 않습니다."));

		TimeVote timeVote = timeVoteRepository.findAllByTimeVoteRoomAndMember(timeVoteRoom, member).get(0);
		Assertions.assertEquals(timeVote.getMemberAvailableStartTime(), startTime);
		Assertions.assertEquals(timeVote.getMemberAvailableEndTime(), endTime);
		Assertions.assertEquals(timeVote.getMeetingDate().getDate(), localDate);
		Assertions.assertEquals(timeVote.getMember().getId(), member.getId());
	}

	@Test
	@DisplayName("해당 방의 회원이 아닌 회원이 시간 투표하기를 하면 실패한다.")
	void 해당방의회원이아닌회원이시간투표하기를하면실패한다() throws Exception {
		// given
		// 방 생성
		final String roomId = "roomId";
		Room room = Room.builder()
			.id(roomId)
			.name("roomName")
			.memo("roomMemo")
			.build();
		roomRepository.save(room);

		// 시간 투표방 생성
		final LocalDate localDate = LocalDate.now(); // 만나는 날짜
		TimeVoteRoom timeVoteRoom = new TimeVoteRoom(room);
		meetingDateRepository.save(new MeetingDate(timeVoteRoom, localDate));
		timeVoteRoomRepository.save(timeVoteRoom);

		// 투표할 시간
		final LocalDateTime startTime = localDate.atTime(3, 0); // 만나는 날짜 중 시작 시간
		final LocalDateTime endTime = localDate.atTime(4, 0); // 만나는 날짜 중 끝 시간

		// 시간 투표하기
		CreateTimeVoteRequest request = new CreateTimeVoteRequest(
			List.of(
				new TimeRange(startTime, endTime)
			)
		);

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/time-votes/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + memberAccessToken)
			.param("roomId", roomId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request))
		);

		// then
		resultActions
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("MR-003"));
	}

	@Test
	@DisplayName("시간 투표 방이 존재하지 않으면 실패한다.")
	void 시간투표방이존재하지않으면실패한다() throws Exception {
		// given
		// 방 생성
		final String roomId = "roomId";
		Room room = Room.builder()
			.id(roomId)
			.name("roomName")
			.memo("roomMemo")
			.build();
		roomRepository.save(room);

		Member member = memberRepository.findByEmail(memberEmail).get();

		memberRoomRepository.save(
			MemberRoom.builder()
				.member(member)
				.room(room)
				.build());

		// 투표할 시간
		final LocalDate localDate = LocalDate.now(); // 만나는 날짜
		final LocalDateTime startTime = localDate.atTime(3, 0); // 만나는 날짜 중 시작 시간
		final LocalDateTime endTime = localDate.atTime(4, 0); // 만나는 날짜 중 끝 시간

		// 시간 투표하기
		CreateTimeVoteRequest request = new CreateTimeVoteRequest(
			List.of(
				new TimeRange(startTime, endTime)
			)
		);

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/time-votes/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + memberAccessToken)
			.param("roomId", roomId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request))
		);

		// then
		resultActions
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("V-202"));
	}

	@Test
	@DisplayName("투표한 날짜가 투표 날짜 후보가 아니면 실패한다.")
	void 투표한날짜가투표날짜후보가아니면실패한다() throws Exception {
		// given
		// 방 생성
		final String roomId = "roomId";
		Room room = Room.builder()
			.id(roomId)
			.name("roomName")
			.memo("roomMemo")
			.build();
		roomRepository.save(room);

		Member member = memberRepository.findByEmail(memberEmail).get();

		memberRoomRepository.save(
			MemberRoom.builder()
				.member(member)
				.room(room)
				.build());

		// 만나는 날짜
		final LocalDate localDate = LocalDate.of(2024, Month.JANUARY, 1); // 만나는 날짜

		// 시간 투표방 생성
		TimeVoteRoom timeVoteRoom = new TimeVoteRoom(room);
		meetingDateRepository.save(new MeetingDate(timeVoteRoom, localDate));
		timeVoteRoomRepository.save(timeVoteRoom);

		// 투표할 시간
		final LocalDate wrongLocalDate = LocalDate.of(2024, Month.JANUARY, 2); // 투표 날짜 후보에 없는 날짜
		final LocalDateTime startTime = wrongLocalDate.atTime(3, 0); // 시작 시간
		final LocalDateTime endTime = wrongLocalDate.atTime(4, 0); // 끝 시간

		// 시간 투표하기
		CreateTimeVoteRequest request = new CreateTimeVoteRequest(
			List.of(
				new TimeRange(startTime, endTime)
			)
		);

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/time-votes/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + memberAccessToken)
			.param("roomId", roomId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request))
		);

		// then
		resultActions
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("V-101"));
	}

	@Test
	@DisplayName("이미 투표를 하였으면 실패한다.")
	void 이미투표를하였으면실패한다() throws Exception {
		// given
		// 방 생성
		final String roomId = "roomId";
		Room room = Room.builder()
			.id(roomId)
			.name("roomName")
			.memo("roomMemo")
			.build();
		roomRepository.save(room);

		Member member = memberRepository.findByEmail(memberEmail).get();

		memberRoomRepository.save(
			MemberRoom.builder()
				.member(member)
				.room(room)
				.build());

		// 만나는 날짜
		final LocalDate localDate = LocalDate.now(); // 만나는 날짜
		// 투표할 시간
		final LocalDateTime startTime = localDate.atTime(3, 0); // 시작 시간
		final LocalDateTime endTime = localDate.atTime(4, 0); // 끝 시간

		// 시간 투표방 생성
		TimeVoteRoom timeVoteRoom = new TimeVoteRoom(room);
		MeetingDate meetingDate = new MeetingDate(timeVoteRoom, localDate); // 만나는 날짜
		meetingDateRepository.save(meetingDate);
		timeVoteRoomRepository.save(timeVoteRoom);

		// 투표 저장
		timeVoteRepository.save(new TimeVote(
			timeVoteRoom,
			meetingDate,
			member,
			startTime,
			endTime));

		// 중복 시간 투표 Request
		CreateTimeVoteRequest request = new CreateTimeVoteRequest(
			List.of(
				new TimeRange(startTime, endTime)
			)
		);

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/time-votes/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + memberAccessToken)
			.param("roomId", roomId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request))
		);

		// then
		resultActions
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.code").value("V-301"));
	}
}
