package middle_point_search.backend.domains.memberRoom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

@DisplayName("방에 회원 저장")
public class SaveMemberToRoomTest extends BaseIntegrationTest {

	private String accessToken;
	private String memberEmail;

	@Autowired
	private MemberRoomRepository memberRoomRepository;
	@Autowired
	private RoomRepository roomRepository;
	@Autowired
	private MemberRepository memberRepository;

	@BeforeEach
	public void setUp() throws Exception {
		AccessTokenAndRefreshToken accessTokenAndRefreshToken = signupAndLoginMember(false);

		memberEmail = NO_ADDRESS_MEMBER_EMAIL;
		accessToken = accessTokenAndRefreshToken.accessToken();
	}

	@Test
	@DisplayName("방에 회원 저장에 성공한다.")
	public void 방에회원저장성공() throws Exception {
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

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/member-rooms/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + accessToken)
			.accept(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(status().isOk());

		// 방에 회원 저장 확인
		Assertions.assertTrue(memberRoomRepository.existsByMember_IdAndRoom_Id(member.getId(), roomId));
	}

	@Test
	@DisplayName("방이 존재하지 않으면 R-201을 반환한다.")
	public void 방이존재하지않으면_R_201을반환() throws Exception {
		// given
		String nonExistRoomId = "nonExistRoomId";

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/member-rooms/rooms/{roomId}", nonExistRoomId)
			.header("Authorization", "Bearer " + accessToken)
			.accept(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(jsonPath("$.code").value("R-201"));
	}


	@Test
	@DisplayName("해당 방에 이미 존재하는 회원이면 MR-002를 반환한다.")
	public void 해당방에이미존재하는회원이면_MR_002를반환() throws Exception {
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
		ResultActions resultActions = mockMvc.perform(post("/api/member-rooms/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + accessToken)
			.accept(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(jsonPath("$.code").value("MR-002"));
	}
}
