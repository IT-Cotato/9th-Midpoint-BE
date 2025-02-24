package middle_point_search.backend.domains.room;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import middle_point_search.backend.common.BaseIntegrationTest;
import middle_point_search.backend.common.dto.AccessTokenAndRefreshToken;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.repository.MemberRepository;
import middle_point_search.backend.domains.memberRoom.service.MemberRoomService;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.dto.request.UpdateRoomMemoRequest;
import middle_point_search.backend.domains.room.dto.request.UpdateRoomNameRequest;
import middle_point_search.backend.domains.room.repository.RoomRepository;

@DisplayName("방 수정")
public class UpdateRoomIntegrationTest extends BaseIntegrationTest {

	@Autowired
	private RoomRepository roomRepository;
	@Autowired
	private MemberRoomService memberRoomService;
	@Autowired
	private MemberRepository memberRepository;

	private String member1AccessToken;
	private String member2AccessToken;

	private String roomId;

	@BeforeEach
	public void setUp() throws Exception {
		// 회원가입 및 로그인
		AccessTokenAndRefreshToken accessTokenAndRefreshToken = signupAndLoginMember(false);
		member1AccessToken = accessTokenAndRefreshToken.accessToken();
		String member1Email = NO_ADDRESS_MEMBER_EMAIL;

		AccessTokenAndRefreshToken accessTokenAndRefreshToken2 = signupAndLoginMember(true);
		member2AccessToken = accessTokenAndRefreshToken2.accessToken();

		// 방 생성
		roomId = "roomId";
		Room room = Room.builder()
			.id(roomId)
			.name("oldRoomName")
			.memo("oldRoomMemo")
			.build();

		roomRepository.save(room);

		// 회원1을 방에 등록
		Member member = memberRepository.findByEmail(member1Email).get();
		memberRoomService.saveMemberToRoom(member, roomId);
	}

	@Nested
	@DisplayName("방 이름 수정")
	class 방_이름_수정 {

		@Test
		@DisplayName("방이름 수정에 성공한다.")
		public void 방이름수정성공() throws Exception {
			// given
			String newRoomName = "수정된 방 이름";
			Room room = roomRepository.findById(roomId).get();

			UpdateRoomNameRequest request = new UpdateRoomNameRequest(newRoomName);

			// when
			ResultActions resultActions = mockMvc.perform(patch("/api/rooms/{roomId}/name", roomId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header("Authorization", "Bearer " + member1AccessToken)
				.accept(MediaType.APPLICATION_JSON));

			// then
			resultActions.andExpect(status().isOk());
			Assertions.assertEquals(newRoomName, room.getName());
		}

		private static Stream<String> provideInvalidName() {
			return Stream.of(
				null,
				"",
				"12345678901234567890123456789012345678901234567890123456789012345"
			);
		}

		@ParameterizedTest
		@MethodSource("provideInvalidName")
		@DisplayName("방 이름이 형식에 맞지 않으면 방 이름 수정에 실패한다.")
		public void 방이름형식에맞지않음_방이름수정실패(String name) throws Exception {
			// given
			UpdateRoomNameRequest request = new UpdateRoomNameRequest(name);

			// when
			ResultActions resultActions = mockMvc.perform(patch("/api/rooms/{roomId}/name", roomId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header("Authorization", "Bearer " + member1AccessToken)
				.accept(MediaType.APPLICATION_JSON));

			// then
			resultActions.andExpect(jsonPath("$.code").value("C-202"));
		}

		@Test
		@DisplayName("방이 존재하지 않으면 방 이름 수정에 실패한다.")
		public void 방이존재하지않음_방이름수정실패() throws Exception {
			// given
			String newRoomName = "수정된 방 이름";
			String notExistRoomId = "notExistRoomId";

			UpdateRoomNameRequest request = new UpdateRoomNameRequest(newRoomName);

			// when
			ResultActions resultActions = mockMvc.perform(patch("/api/rooms/{roomId}/name", notExistRoomId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header("Authorization", "Bearer " + member1AccessToken)
				.accept(MediaType.APPLICATION_JSON));

			// then
			resultActions.andExpect(jsonPath("$.code").value("R-201"));
		}

		@Test
		@DisplayName("해당 방의 회원이 아니면 방 이름 수정에 실패한다.")
		public void 해당방의회원이아님_방이름수정실패() throws Exception {
			// given
			String newRoomName = "수정된 방 이름";

			UpdateRoomNameRequest request = new UpdateRoomNameRequest(newRoomName);

			// when
			ResultActions resultActions = mockMvc.perform(patch("/api/rooms/{roomId}/name", roomId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header("Authorization", "Bearer " + member2AccessToken)
				.accept(MediaType.APPLICATION_JSON));

			// then
			resultActions.andExpect(jsonPath("$.code").value("MR-003"));
		}
	}

	@Nested
	@DisplayName("방 메모 수정")
	class 방_메모_수정 {

		@Test
		@DisplayName("방메모 수정에 성공한다.")
		public void 방메모수정성공() throws Exception {
			// given
			String newRoomMemo = "수정된 방 메모";
			Room room = roomRepository.findById(roomId).get();

			UpdateRoomMemoRequest request = new UpdateRoomMemoRequest(newRoomMemo);

			// when
			ResultActions resultActions = mockMvc.perform(patch("/api/rooms/{roomId}/memo", roomId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header("Authorization", "Bearer " + member1AccessToken)
				.accept(MediaType.APPLICATION_JSON));

			// then
			resultActions.andExpect(status().isOk());
			Assertions.assertEquals(newRoomMemo, room.getMemo());
		}

		@Test
		@DisplayName("방이 존재하지 않으면 방 메모 수정에 실패한다.")
		public void 방이존재하지않음_방메모수정실패() throws Exception {
			// given
			String newRoomMemo = "수정된 방 메모";
			String notExistRoomId = "notExistRoomId";

			UpdateRoomMemoRequest request = new UpdateRoomMemoRequest(newRoomMemo);

			// when
			ResultActions resultActions = mockMvc.perform(patch("/api/rooms/{roomId}/memo", notExistRoomId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header("Authorization", "Bearer " + member1AccessToken)
				.accept(MediaType.APPLICATION_JSON));

			// then
			resultActions.andExpect(jsonPath("$.code").value("R-201"));
		}

		@Test
		@DisplayName("해당 방의 회원이 아니면 방 메모 수정에 실패한다.")
		public void 해당방의회원이아님_방메모수정실패() throws Exception {
			// given
			String newRoomMemo = "수정된 방 메모";

			UpdateRoomMemoRequest request = new UpdateRoomMemoRequest(newRoomMemo);

			// when
			ResultActions resultActions = mockMvc.perform(patch("/api/rooms/{roomId}/memo", roomId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header("Authorization", "Bearer " + member2AccessToken)
				.accept(MediaType.APPLICATION_JSON));

			// then
			resultActions.andExpect(jsonPath("$.code").value("MR-003"));
		}
	}
}
