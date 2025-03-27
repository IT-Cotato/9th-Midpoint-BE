package middle_point_search.backend.domains.room.service;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.domains.memberRoom.repository.MemberRoomRepository;
import middle_point_search.backend.domains.memberRoom.service.MemberRoomValidateService;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.dto.request.CreateRoomRequest;
import middle_point_search.backend.domains.room.dto.request.UpdateRoomMemoRequest;
import middle_point_search.backend.domains.room.dto.request.UpdateRoomNameRequest;
import middle_point_search.backend.domains.room.dto.response.CreateRoomResponse;
import middle_point_search.backend.domains.room.dto.response.ExistRoomResponse;
import middle_point_search.backend.domains.room.dto.response.FindRoomDetailResponse;
import middle_point_search.backend.domains.room.repository.RoomRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoomService {

	private final RoomRepository roomRepository;
	private final MemberRoomValidateService memberRoomValidateService;
	private final MemberRoomRepository memberRoomRepository;
	private final RoomValidationService roomValidationService;

	// Room 저장하기 및 Room에 회원 저장
	@Transactional
	public CreateRoomResponse createRoom(CreateRoomRequest request) {
		String memo = makeMemoNullToBlank(request.memo());

		Room room = Room.builder()
			.name(request.name())
			.memo(memo)
			.id(UUID.randomUUID().toString())
			.build();

		return CreateRoomResponse.from(roomRepository.save(room).getId());
	}

	// Room 이름 변경하기
	@Transactional(rollbackFor = CustomException.class)
	public void updateRoomName(Long memberId, String roomId, UpdateRoomNameRequest request) {
		roomValidationService.validateRoomExisting(roomId);
		memberRoomValidateService.validateAuthorizedMember(memberId, roomId);

		// 변경
		roomRepository.findById(roomId)
			.orElseThrow(() -> CustomException.from(ROOM_NOT_FOUND))
			.updateName(request.name());
	}

	// 방 존재 확인
	public ExistRoomResponse existRoom(String roomId) {
		return ExistRoomResponse.from(roomRepository.existsById(roomId));
	}

	// Room 메모 변경하기
	@Transactional
	public void updateRoomMemo(Long memberId, String roomId, UpdateRoomMemoRequest request) {
		roomValidationService.validateRoomExisting(roomId);
		memberRoomValidateService.validateAuthorizedMember(memberId, roomId);

		// 변경
		String memo = makeMemoNullToBlank(request.memo());

		roomRepository.findById(roomId)
			.orElseThrow(() -> CustomException.from(ROOM_NOT_FOUND))
			.updateMemo(memo);
	}

	// 메모가 null일 경우 ""로 변경
	private String makeMemoNullToBlank(String memo) {
		if (memo == null) {
			return "";
		}
		return memo;
	}

	// 방 상세 조회
	public FindRoomDetailResponse findRoomDetail(Long memberId, String roomId) {
		roomValidationService.validateRoomExisting(roomId);
		memberRoomValidateService.validateAuthorizedMember(memberId, roomId);

		// 조회
		Room room = roomRepository.findById(roomId)
			.orElseThrow(() -> CustomException.from(ROOM_NOT_FOUND));
		List<String> emails = memberRoomRepository.findAllByRoomId(roomId)
			.stream()
			.map(memberRoom -> memberRoom.getMember().getEmail())
			.toList();

		return FindRoomDetailResponse.from(room, emails);
	}
}
