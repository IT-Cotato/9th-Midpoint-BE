package middle_point_search.backend.domains.room;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import middle_point_search.backend.common.BaseIntegrationTest;
import middle_point_search.backend.common.dto.AccessTokenAndRefreshToken;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.dto.request.CreateRoomRequest;
import middle_point_search.backend.domains.room.dto.response.CreateRoomResponse;
import middle_point_search.backend.domains.room.repository.RoomRepository;

@DisplayName("방 생성")
public class CreateRoomIntegrationTest extends BaseIntegrationTest {

	private String accessToken;

	@Autowired
	private RoomRepository roomRepository;

	@BeforeEach
	public void setUp() throws Exception {
		AccessTokenAndRefreshToken accessTokenAndRefreshToken = signupAndLoginMember(false);

		accessToken = accessTokenAndRefreshToken.accessToken();
	}

	@Test
	@DisplayName("방 생성에 성공한다.")
	public void 방생성성공() throws Exception {
		// given
		String roomName = "방 이름";
		String memo = "방 메모";

		CreateRoomRequest request = new CreateRoomRequest(roomName, memo);

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/rooms")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request))
			.header("Authorization", "Bearer " + accessToken)
			.accept(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").exists());

		CreateRoomResponse response = getResponseData(resultActions, CreateRoomResponse.class);
		Room room = roomRepository.findById(response.id()).get();

		Assertions.assertEquals(roomName, room.getName());
		Assertions.assertEquals(memo, room.getMemo());
	}

	private static Stream<String> provideInvalidName() {
		return Stream.of(
			null,
			"",
			"12345678901234567890123456789012345678901234567890123456789012345"
		);
	}

	@ParameterizedTest
	@DisplayName("방 이름이 형식에 맞지 않으면 방 생성에 실패한다.")
	@MethodSource("provideInvalidName")
	public void 방이름형식에맞지않음_방생성실패(String name) throws Exception {
		// given
		String memo = "방 메모";

		CreateRoomRequest request = new CreateRoomRequest(name, memo);

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/rooms")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request))
			.header("Authorization", "Bearer " + accessToken)
			.accept(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("C-202"));
	}
}
