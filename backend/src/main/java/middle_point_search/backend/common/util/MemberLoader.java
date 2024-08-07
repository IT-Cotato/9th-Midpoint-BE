package middle_point_search.backend.common.util;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.common.security.dto.MemberAuthenticationToken;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.repository.MemberRepository;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.repository.RoomRepository;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberLoader {

	private final MemberRepository memberRepository;
	private final RoomRepository roomRepository;

	@Transactional
	public Member getMember() {
		String roomId = getRoomId();
		String name = getName();

		return memberRepository.findByRoom_IdentityNumberAndName(roomId, name)
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
	}

	@Transactional
	public Room getRoom() {
		String roomId = getRoomId();

		return roomRepository.findByIdentityNumber(roomId)
			.orElseThrow(() -> new CustomException(ROOM_NOT_FOUND));
	}

	public String getName() {
		MemberAuthenticationToken authentication = (MemberAuthenticationToken)SecurityContextHolder.getContext()
			.getAuthentication();
		return (String)authentication.getName();
	}

	public String getRoomId() {
		MemberAuthenticationToken authentication = (MemberAuthenticationToken)SecurityContextHolder.getContext()
			.getAuthentication();
		return (String)authentication.getRoomId();
	}
}
