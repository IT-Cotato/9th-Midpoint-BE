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

@DisplayName("회원방 존재 여부 테스트")
public class ExistsMemberRoomTest extends BaseIntegrationTest {

	private String memberAccessToken;
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
		memberAccessToken = accessTokenAndRefreshToken.accessToken();
	}

	@Test
	@DisplayName("방에 회원이 존재하면 true를 반환한다.")
	void 방에_회원이_존재하면_true를_반환한다() throws Exception {
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
		ResultActions resultActions = mockMvc.perform(get("/api/member-rooms/exists/rooms/" + roomId)
			.header("Authorization", "Bearer " + memberAccessToken)
			.contentType(MediaType.APPLICATION_JSON));

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.exists").value(true));
	}

	@Test
	@DisplayName("방에 회원이 존재하지 않으면 false를 반환한다.")
	void 방에_회원이_존재하지_않으면_false를_반환한다() throws Exception {
		// given
		// 방 생성
		String roomId = "roomId";
		Room room = roomRepository.save(Room.builder()
			.id(roomId)
			.name("roomName")
			.memo("roomMemo")
			.build());

		// when
		ResultActions resultActions = mockMvc.perform(get("/api/member-rooms/exists/rooms/" + roomId)
			.header("Authorization", "Bearer " + memberAccessToken)
			.contentType(MediaType.APPLICATION_JSON));

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.exists").value(false));
	}
}
