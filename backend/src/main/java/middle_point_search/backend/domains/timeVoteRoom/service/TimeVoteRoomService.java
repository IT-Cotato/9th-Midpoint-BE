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

	// 시간 투표방 생성
	@Transactional(rollbackFor = {CustomException.class})
	public TimeVoteRoomCreateResponse createTimeVoteRoom(Long roomId, TimeVoteRoomCreateRequest request) {
		boolean exists = timeVoteRoomRepository.existsByRoom_Id(roomId);

		// 방존재여부 확인
		if (exists) {
			throw CustomException.from(DUPLICATE_VOTE_ROOM);
		}

		// 방 조회
		Room room = roomService.findRoom(roomId)
			.orElseThrow(() -> CustomException.from(ROOM_NOT_FOUND));

		TimeVoteRoom timeVoteRoom = new TimeVoteRoom(room, request.getDates());
		TimeVoteRoom savedTimeVoteRoom = timeVoteRoomRepository.save(timeVoteRoom);

		return TimeVoteRoomCreateResponse.from(savedTimeVoteRoom.getId());
	}

	//시간투표방 재생성하기
	@Transactional(rollbackFor = {CustomException.class})
	public TimeVoteRoomCreateResponse recreateTimeVoteRoom(Long roomId, TimeVoteRoomCreateRequest request) {
		// 기존 투표방 삭제
		timeVoteRoomRepository.deleteByRoom_Id(roomId);

		// 방 조회
		Room room = roomService.findRoom(roomId)
			.orElseThrow(() -> CustomException.from(ROOM_NOT_FOUND));

		// 새로운 투표방 생성
		TimeVoteRoom timeVoteRoom = new TimeVoteRoom(room, request.getDates());
		TimeVoteRoom savedTimeVoteRoom = timeVoteRoomRepository.save(timeVoteRoom);

		return TimeVoteRoomCreateResponse.from(savedTimeVoteRoom.getId());
	}

	// 시간투표방 조회
	public TimeVoteRoomGetResponse findTimeVoteRoomAndMakeDTO(Long roomId) {
		Optional<TimeVoteRoom> timeVoteRoomOptional = timeVoteRoomRepository.findByRoom_Id(roomId);

		return timeVoteRoomOptional
			.map(timeVoteRoom -> {
				List<LocalDate> dates = timeVoteRoom.getMeetingDates()
					.stream()
					.map(MeetingDate::getDate)
					.toList();
				return TimeVoteRoomGetResponse.from(true, dates);
			})
			.orElseGet(() -> TimeVoteRoomGetResponse.from(false, null));
	}

	// 시간 투표방, 방으로 조회
	public Optional<TimeVoteRoom> findByRoomId(Long roomId) {
		return timeVoteRoomRepository.findByRoom_Id(roomId);
	}
}
