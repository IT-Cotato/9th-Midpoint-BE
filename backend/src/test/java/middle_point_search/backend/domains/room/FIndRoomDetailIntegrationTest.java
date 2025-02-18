package middle_point_search.backend.domains.room;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

@DisplayName("방 상세 조회")
public class FIndRoomDetailIntegrationTest extends BaseIntegrationTest {

	private String accessToken;
	private String memberEmail;

	private final String roomId = "roomId";
	private final String roomName = "roomName";
	private final String roomMemo = "roomMemo";

	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private MemberRoomRepository memberRoomRepository;
	@Autowired
	private RoomRepository roomRepository;

	@BeforeEach
	public void setUp() throws Exception {
		// given
		// 회원가입 및 로그인
		AccessTokenAndRefreshToken accessTokenAndRefreshToken = signupAndLoginMember(false);
		accessToken = accessTokenAndRefreshToken.accessToken();
		memberEmail = NO_ADDRESS_MEMBER_EMAIL;

		// 방 생성
		Room room = Room.builder()
			.id(roomId)
			.name(roomName)
			.memo(roomMemo)
			.build();
		roomRepository.save(room);

		// 방에 회원 저장
		Member member = memberRepository.findByEmail(memberEmail).get();
		MemberRoom memberRoom = MemberRoom.builder()
			.member(member)
			.room(room)
			.build();
		memberRoomRepository.save(memberRoom);
	}

	@Test
	@DisplayName("방 상세 조회에 성공한다.")
	public void 방상세조회성공() throws Exception {
		// when
		ResultActions resultActions = mockMvc.perform(get("/api/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + accessToken));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.name").value(roomName))
			.andExpect(jsonPath("$.data.memo").value(roomMemo))
			.andExpect(jsonPath("$.data.memberCount").value(1))
			.andExpect(jsonPath("$.data.emails[0]").value(memberEmail));
	}

	@Test
	@DisplayName("해당 방의 회원이 아니면 방 상세 조회에 실패한다.")
	public void 해당방의회원이아니면_방상세조회실패() throws Exception {
		// given
		// 회원가입 및 로그인
		AccessTokenAndRefreshToken accessTokenAndRefreshToken = signupAndLoginMember(true);
		String anotherAccessToken = accessTokenAndRefreshToken.accessToken();

		// when
		ResultActions resultActions = mockMvc.perform(get("/api/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + anotherAccessToken));

		// then
		resultActions
			.andExpect(jsonPath("$.code").value("MR-003"));
	}

	@Test
	@DisplayName("존재하지 않는 방이면 방 상세 조회에 실패한다.")
	public void 존재하지않는방이면_방상세조회실패() throws Exception {
		// given
		String nonExistRoomId = "nonExistRoomId";

		// when
		ResultActions resultActions = mockMvc.perform(get("/api/rooms/{roomId}", nonExistRoomId)
			.header("Authorization", "Bearer " + accessToken));

		// then
		resultActions
			.andExpect(jsonPath("$.code").value("R-201"));
	}
}
