package middle_point_search.backend.domains.member;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import middle_point_search.backend.common.BaseIntegrationTest;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.common.dto.AccessTokenAndRefreshToken;
import middle_point_search.backend.domains.member.dto.request.DeleteMemberRequest;
import middle_point_search.backend.domains.member.repository.MemberRepository;
import middle_point_search.backend.domains.memberRoom.domain.MemberRoom;
import middle_point_search.backend.domains.memberRoom.repository.MemberRoomRepository;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.repository.RoomRepository;

@DisplayName("회원 삭제")
public class DeleteMemberIntegrationTest extends BaseIntegrationTest {

	@Autowired
	MemberRoomRepository memberRoomRepository;
	@Autowired
	RoomRepository roomRepository;
	@Autowired
	private MemberRepository memberRepository;

	private String accessToken;

	@BeforeEach
	public void setUp() throws Exception {
		// 회원가입 및 로그인
		AccessTokenAndRefreshToken accessTokenAndRefreshToken = signupAndLoginNoAddressMember();
		accessToken = accessTokenAndRefreshToken.accessToken();
	}

	@Test
	@DisplayName("회원 삭제 및 관련 정보 삭제를 성공한다.")
	public void 회원삭제성공() throws Exception {
		// given
		// 회원 조회
		Member member = memberRepository.findByEmail(NO_ADDRESS_MEMBER_EMAIL).get();
		Long memberId = member.getId();

		// 방 생성
		String roomId = "roomId";
		Room room = Room.builder()
			.id(roomId)
			.memo("memo")
			.name("title")
			.build();
		roomRepository.save(room);

		// 방에 회원 저장
		MemberRoom memberRoom = MemberRoom.builder()
			.member(member)
			.room(room)
			.build();
		memberRoomRepository.save(memberRoom);

		DeleteMemberRequest deleteMemberRequest = new DeleteMemberRequest("탈퇴 사유");

		// when
		ResultActions resultActions = mockMvc.perform(delete("/api/members/delete")
			.header("Authorization", "Bearer " + accessToken)
			.contentType("application/json")
			.content(objectMapper.writeValueAsString(deleteMemberRequest))
			.accept("application/json")
		);

		// then
		resultActions
			.andExpect(status().isOk());
		// 회원 삭제 확인
		boolean isDeleted = memberRepository.findByEmail(NO_ADDRESS_MEMBER_EMAIL).isEmpty();
		assertThat(isDeleted).isTrue();

		// 방에 회원 삭제 확인
		boolean isMemberRoomDeleted = memberRoomRepository.existsByMember_IdAndRoom_Id(memberId, roomId);
		assertThat(isMemberRoomDeleted).isFalse();

		// // 방에 인원 없으면 방 삭제 확인
		// boolean isRoomDeleted = roomRepository.existsById(roomId);
		// assertThat(isRoomDeleted).isFalse();
	}
}
