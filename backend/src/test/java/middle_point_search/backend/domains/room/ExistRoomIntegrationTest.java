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
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.repository.RoomRepository;

@DisplayName("방 존재 확인")
public class ExistRoomIntegrationTest extends BaseIntegrationTest {

	private String accessToken;
	private String existRoomId;

	@Autowired
	private RoomRepository roomRepository;

	@BeforeEach
	public void setUp() throws Exception {
		AccessTokenAndRefreshToken accessTokenAndRefreshToken = signupAndLoginMember(false);

		accessToken = accessTokenAndRefreshToken.accessToken();

		// 방 생성
		existRoomId = "existRoomId";
		roomRepository.save(Room.builder()
			.id(existRoomId)
			.name("existRoomName")
			.memo("existRoomMemo")
			.build());
	}

	@Test
	@DisplayName("방이 존재하면 true를 반환한다.")
	public void 방이존재하면_true를반환() throws Exception {
		// when
		ResultActions resultActions = mockMvc.perform(get("/api/rooms/{roomId}/existence", existRoomId)
			.header("Authorization", "Bearer " + accessToken));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.existence").value(true));
	}

	@Test
	@DisplayName("방이 존재하지 않으면 false를 반환한다.")
	public void 방이존재하지않으면_false를반환() throws Exception {
		String nonExistRoomId = "nonExistRoomId";
		// when
		ResultActions resultActions = mockMvc.perform(get("/api/rooms/{roomId}/existence", nonExistRoomId)
			.header("Authorization", "Bearer " + accessToken));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.existence").value(false));
	}
}
