package middle_point_search.backend.domains.memberRoom;

import static org.junit.jupiter.api.Assertions.*;
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

@DisplayName("방에서 회원 삭제")
public class DeleteMemberFromRoomTest extends BaseIntegrationTest {
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
	@DisplayName("방에서 회원 삭제에 성공한다.")
	void 방에서회원삭제성공() throws Exception {
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
		MemberRoom memberRoom = memberRoomRepository.save(MemberRoom.builder()
			.member(member)
			.room(room)
			.build());

		// when
		ResultActions resultActions = mockMvc.perform(delete("/api/member-rooms/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + memberAccessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON));

		// then
		resultActions
			.andExpect(status().isOk());
		// 방에서 회원 삭제 확인
		assertFalse(memberRoomRepository.existsById(memberRoom.getId()));
	}

	@Test
	@DisplayName("방에서 회원 삭제에 실패한다. - 방의 회원이 아님")
	void 방에서회원삭제실패_방의회원이아님() throws Exception {
		// given
		// 방 생성
		String roomId = "roomId";
		Room room = Room.builder()
			.id(roomId)
			.name("roomName")
			.memo("roomMemo")
			.build();
		roomRepository.save(room);

		// when
		ResultActions resultActions = mockMvc.perform(delete("/api/member-rooms/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + memberAccessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON));

		// then
		resultActions
			.andExpect(jsonPath("$.code").value("MR-003"));
	}

	@Test
	@DisplayName("방에서 회원 삭제에 실패한다. - 방이 존재하지 않음")
	void 방에서회원삭제실패_방이존재하지않음() throws Exception {
		// given
		// when
		ResultActions resultActions = mockMvc.perform(delete("/api/member-rooms/rooms/{roomId}", "roomId")
			.header("Authorization", "Bearer " + memberAccessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON));

		// then
		resultActions
			.andExpect(jsonPath("$.code").value("R-201"));
	}

	@Test
	@DisplayName("회원 삭제시 방에 아무도 없으면 방도 삭제된다.")
	void 회원삭제시방에아무도없으면방도삭제된다() throws Exception {
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
		MemberRoom memberRoom = memberRoomRepository.save(MemberRoom.builder()
			.member(member)
			.room(room)
			.build());

		// when
		ResultActions resultActions = mockMvc.perform(delete("/api/member-rooms/rooms/{roomId}", roomId)
			.header("Authorization", "Bearer " + memberAccessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON));

		// then
		resultActions
			.andExpect(status().isOk());
		// 방 삭제 확인
		assertFalse(roomRepository.existsById(roomId));
	}
}
