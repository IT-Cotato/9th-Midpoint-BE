package middle_point_search.backend.domains.memberRoom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

@DisplayName("회원이 속한 방들 조회")
public class FindRoomsTest extends BaseIntegrationTest {

	private String accessToken;
	private String memberEmail;

	@Autowired
	private MemberRoomRepository memberRoomRepository;
	@Autowired
	private RoomRepository roomRepository;
	@Autowired
	private MemberRepository memberRepository;

	@BeforeEach
	void setUp() throws Exception {
		AccessTokenAndRefreshToken accessTokenAndRefreshToken = signupAndLoginMember(false);

		memberEmail = NO_ADDRESS_MEMBER_EMAIL;
		accessToken = accessTokenAndRefreshToken.accessToken();
	}

	@Test
	@DisplayName("회원이 속한 방들을 조회한다.")
	void 회원이_속한_방들_조회() throws Exception {
		// given
		// 방 생성
		String roomId = "roomId";
		String roomName = "roomName";
		Room room = roomRepository.save(Room.builder()
			.id(roomId)
			.name(roomName)
			.memo("roomMemo")
			.build());

		// 회원 id 조회
		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

		// 방에 회원 저장
		memberRoomRepository.save(MemberRoom.builder()
			.member(member)
			.room(room)
			.build());

		// when
		// 방 조회
		ResultActions resultActions = mockMvc.perform(get("/api/member-rooms")
			.header("Authorization", "Bearer " + accessToken)
			.accept(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.data[0].roomId").value(roomId))
			.andExpect(jsonPath("$.data[0].roomName").value(roomName));
	}

	@Test
	@DisplayName("회원이 속한 방이 없으면 빈 리스트를 반환한다.")
	void 회원이_속한_방이_없으면_빈_리스트를_반환() throws Exception {
		// when
		// 방 조회
		ResultActions resultActions = mockMvc.perform(get("/api/member-rooms")
			.header("Authorization", "Bearer " + accessToken)
			.accept(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.data").isEmpty());
	}

	@Test
	@DisplayName("회원이 속한 방이 여러개일 때 모두 조회한다.")
	void 회원이_속한_방이_여러개일_때_모두_조회() throws Exception {
		// given
		// 방 생성
		String roomId1 = "roomId1";
		String roomName1 = "roomName1";
		Room room1 = roomRepository.save(Room.builder()
			.id(roomId1)
			.name(roomName1)
			.memo("roomMemo1")
			.build());

		String roomId2 = "roomId2";
		String roomName2 = "roomName2";
		Room room2 = roomRepository.save(Room.builder()
			.id(roomId2)
			.name(roomName2)
			.memo("roomMemo2")
			.build());

		// 회원 id 조회
		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

		// 방에 회원 저장
		memberRoomRepository.save(MemberRoom.builder()
			.member(member)
			.room(room1)
			.build());
		memberRoomRepository.save(MemberRoom.builder()
			.member(member)
			.room(room2)
			.build());

		// when
		// 방 조회
		ResultActions resultActions = mockMvc.perform(get("/api/member-rooms")
			.header("Authorization", "Bearer " + accessToken)
			.accept(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.data[0].roomId").value(roomId1))
			.andExpect(jsonPath("$.data[0].roomName").value(roomName1))
			.andExpect(jsonPath("$.data[1].roomId").value(roomId2))
			.andExpect(jsonPath("$.data[1].roomName").value(roomName2));
	}
}
