package middle_point_search.backend.domains.timeVote;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
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
import middle_point_search.backend.domains.timeVoteRoom.domain.TimeVoteRoom;
import middle_point_search.backend.domains.timeVoteRoom.dto.request.UpdateTimeVoteRoomRequest;
import middle_point_search.backend.domains.timeVoteRoom.repository.TimeVoteRoomRepository;

@DisplayName("시간 투표 방 수정 테스트")
public class UpdateTimeVoteRoomTest extends BaseIntegrationTest {

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

	// 시간 투표방 수정에 성공한다.
	@Test
	@DisplayName("시간 투표 방 수정에 성공한다.")
	void 시간투표방수정성공() throws Exception {
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

		// 회원 방 생성
		MemberRoom memberRoom = MemberRoom.builder()
			.member(member)
			.room(room)
			.build();
		memberRoomRepository.save(memberRoom);

		// 시간 투표방 생성
		timeVoteRoomRepository.save(new TimeVoteRoom(room));

		// when
		LocalDate localDate = LocalDate.now();
		UpdateTimeVoteRoomRequest updateTimeVoteRoomRequest = new UpdateTimeVoteRoomRequest(
			List.of(localDate)
		);

		ResultActions resultActions = mockMvc.perform(put("/api/time-vote-rooms/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(updateTimeVoteRoomRequest)));

		// then
		resultActions
			.andExpect(status().isOk());

		// 시간 투표방 수정 확인
		TimeVoteRoom timeVoteRoom = timeVoteRoomRepository.findByRoom_Id(roomId)
			.orElseThrow(() -> new IllegalArgumentException("시간 투표방을 찾을 수 없습니다."));
		Assertions.assertEquals(localDate, timeVoteRoom.getMeetingDates().get(0).getDate());
	}

	@Test
	@DisplayName("해당 방의 회원이 아닌 회원이 시간 투표 방을 수정하면 실패한다.")
	void 해당방의회원이아닌회원이시간투표방을수정하면실패한다() throws Exception {
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

		// 시간 투표방 생성
		timeVoteRoomRepository.save(new TimeVoteRoom(room));

		// when
		LocalDate localDate = LocalDate.now();
		UpdateTimeVoteRoomRequest updateTimeVoteRoomRequest = new UpdateTimeVoteRoomRequest(
			List.of(localDate)
		);

		ResultActions resultActions = mockMvc.perform(put("/api/time-vote-rooms/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(updateTimeVoteRoomRequest)));

		// then
		resultActions
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("MR-003"));
	}

	@Test
	@DisplayName("시간 투표 방이 존재하지 않으면 수정에 실패한다.")
	void 시간투표방이존재하지않으면수정에실패한다() throws Exception {
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

		// 회원 방 생성
		MemberRoom memberRoom = MemberRoom.builder()
			.member(member)
			.room(room)
			.build();
		memberRoomRepository.save(memberRoom);

		// when
		LocalDate localDate = LocalDate.now();
		UpdateTimeVoteRoomRequest updateTimeVoteRoomRequest = new UpdateTimeVoteRoomRequest(
			List.of(localDate)
		);

		ResultActions resultActions = mockMvc.perform(put("/api/time-vote-rooms/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(updateTimeVoteRoomRequest)));

		// then
		resultActions
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("TV-001"));
	}
}
