package middle_point_search.backend.domains.timeVoteRoom.service;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;
import static middle_point_search.backend.domains.timeVoteRoom.dto.TimeVoteRoomDTO.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.domains.memberRoom.service.MemberRoomValidateService;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.service.RoomService;
import middle_point_search.backend.domains.timeVoteRoom.domain.MeetingDate;
import middle_point_search.backend.domains.timeVoteRoom.domain.TimeVoteRoom;
import middle_point_search.backend.domains.timeVoteRoom.repository.TimeVoteRoomRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimeVoteRoomService {

	private final TimeVoteRoomRepository timeVoteRoomRepository;
	private final RoomService roomService;
	private final MemberRoomValidateService memberRoomValidateService;

	// 시간 투표방 생성
	@Transactional(rollbackFor = {CustomException.class})
	public CreateTimeVoteRoomResponse createTimeVoteRoom(Long memberId, String roomId, CreateTimeVoteRoomRequest request) {
		// 방에 대한 회원인지 확인
		memberRoomValidateService.validateAuthorizedMember(memberId, roomId);

		boolean exists = timeVoteRoomRepository.existsByRoom_Id(roomId);

		// 방존재여부 확인
		if (exists) {
			throw CustomException.from(DUPLICATE_VOTE_ROOM);
		}

		// 방 조회
		Room room = roomService.findRoom(roomId)
			.orElseThrow(() -> CustomException.from(ROOM_NOT_FOUND));

		// 시간 투표방 생성
		TimeVoteRoom timeVoteRoom = new TimeVoteRoom(room);
		request.getDates().stream()
			.map(date -> new MeetingDate(timeVoteRoom, date))
			.forEach(timeVoteRoom::addMeetingDate);
		TimeVoteRoom savedTimeVoteRoom = timeVoteRoomRepository.save(timeVoteRoom);

		return CreateTimeVoteRoomResponse.from(savedTimeVoteRoom.getId());
	}

	//시간투표방 변경하기
	@Transactional(rollbackFor = {CustomException.class})
	public void updateTimeVoteRoom(Long memberId, String roomId, UpdateTimeVoteRoomRequest request) {
		// 방에 대한 회원인지 확인
		memberRoomValidateService.validateAuthorizedMember(memberId, roomId);

		// 시간 투표방 조회
		TimeVoteRoom timeVoteRoom = timeVoteRoomRepository.findByRoom_Id(roomId)
			.orElseThrow(() -> CustomException.from(TIME_VOTE_ROOM_NOT_FOUND));

		timeVoteRoom.resetTimeVoteRoom();

		request.getDates().stream()
			.map(date -> new MeetingDate(timeVoteRoom, date))
			.forEach(timeVoteRoom::addMeetingDate);
	}

	// 시간투표방 조회
	public FindTimeVoteRoomResponse findTimeVoteRoomAndMakeDTO(Long memberId, String roomId) {
		// 방에 대한 회원인지 확인
		memberRoomValidateService.validateAuthorizedMember(memberId, roomId);

		Optional<TimeVoteRoom> timeVoteRoomOptional = timeVoteRoomRepository.findByRoom_Id(roomId);

		return timeVoteRoomOptional
			.map(timeVoteRoom -> {
				List<LocalDate> dates = timeVoteRoom.getMeetingDates()
					.stream()
					.map(MeetingDate::getDate)
					.toList();
				return FindTimeVoteRoomResponse.from(true, dates);
			})
			.orElseGet(() -> FindTimeVoteRoomResponse.from(false, null));
	}

	// 시간 투표방, 방으로 조회
	public Optional<TimeVoteRoom> findByRoomId(String roomId) {
		return timeVoteRoomRepository.findByRoom_Id(roomId);
	}
}
