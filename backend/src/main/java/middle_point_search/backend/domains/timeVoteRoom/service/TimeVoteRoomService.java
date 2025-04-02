package middle_point_search.backend.domains.timeVoteRoom.service;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.domains.memberRoom.service.MemberRoomValidateService;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.repository.RoomRepository;
import middle_point_search.backend.domains.timeVoteRoom.domain.MeetingDate;
import middle_point_search.backend.domains.timeVoteRoom.domain.TimeVoteRoom;
import middle_point_search.backend.domains.timeVoteRoom.dto.request.CreateTimeVoteRoomRequest;
import middle_point_search.backend.domains.timeVoteRoom.dto.request.UpdateTimeVoteRoomRequest;
import middle_point_search.backend.domains.timeVoteRoom.dto.response.CreateTimeVoteRoomResponse;
import middle_point_search.backend.domains.timeVoteRoom.dto.response.FindTimeVoteRoomResponse;
import middle_point_search.backend.domains.timeVoteRoom.repository.MeetingDateRepository;
import middle_point_search.backend.domains.timeVoteRoom.repository.TimeVoteRepository;
import middle_point_search.backend.domains.timeVoteRoom.repository.TimeVoteRoomRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimeVoteRoomService {

	private final TimeVoteRoomRepository timeVoteRoomRepository;
	private final RoomRepository roomRepository;
	private final MemberRoomValidateService memberRoomValidateService;
	private final TimeVoteRepository timeVoteRepository;
	private final MeetingDateRepository meetingDateRepository;

	// 시간 투표방 생성
	@Transactional(rollbackFor = {CustomException.class})
	public CreateTimeVoteRoomResponse createTimeVoteRoom(Long memberId, String roomId, CreateTimeVoteRoomRequest request) {
		// 방에 대한 회원인지 확인
		memberRoomValidateService.validateAuthorizedMember(memberId, roomId);

		// 방존재여부 확인
		if (timeVoteRoomRepository.existsByRoom_Id(roomId)) {
			throw CustomException.from(DUPLICATE_VOTE_ROOM);
		}

		// 방 조회
		Room room = roomRepository.findById(roomId)
			.orElseThrow(() -> CustomException.from(ROOM_NOT_FOUND));

		// 시간 투표방 생성
		TimeVoteRoom timeVoteRoom = new TimeVoteRoom(room);
		request.dates()
			.stream()
			.map(date -> new MeetingDate(timeVoteRoom, date))
			.forEach(meetingDateRepository::save);

		return CreateTimeVoteRoomResponse.from(timeVoteRoomRepository.save(timeVoteRoom).getId());
	}

	//시간투표방 변경하기
	@Transactional(rollbackFor = {CustomException.class})
	public void updateTimeVoteRoom(Long memberId, String roomId, UpdateTimeVoteRoomRequest request) {
		// 방에 대한 회원인지 확인
		memberRoomValidateService.validateAuthorizedMember(memberId, roomId);

		// 시간 투표방 조회
		TimeVoteRoom timeVoteRoom = timeVoteRoomRepository.findByRoom_Id(roomId)
			.orElseThrow(() -> CustomException.from(TIME_VOTE_ROOM_NOT_FOUND));

		// 시간투표방 초기화
		timeVoteRepository.deleteAllByTimeVoteRoom(timeVoteRoom);
		meetingDateRepository.deleteAllByTimeVoteRoom(timeVoteRoom);

		request.dates()
			.stream()
			.map(date -> new MeetingDate(timeVoteRoom, date))
			.forEach(meetingDateRepository::save);
	}

	// 시간투표방 조회
	public FindTimeVoteRoomResponse findTimeVoteRoomAndMakeDTO(Long memberId, String roomId) {
		// 방에 대한 회원인지 확인
		memberRoomValidateService.validateAuthorizedMember(memberId, roomId);

		return timeVoteRoomRepository.findByRoom_Id(roomId)
			.map(timeVoteRoom -> {
				List<LocalDate> dates = meetingDateRepository.findAllByTimeVoteRoom(timeVoteRoom)
					.stream()
					.map(MeetingDate::getDate)
					.toList();

				return FindTimeVoteRoomResponse.from(true, dates);
			})
			.orElseGet(() -> FindTimeVoteRoomResponse.from(false, null));
	}

	// 시간투표방 삭제(시간 투표, 만나는 날도 함께 삭제)
	@Transactional(rollbackFor = {CustomException.class})
	public void deleteTimeVoteRoomAndAssociatedEntities(String roomId) {
		timeVoteRoomRepository.findByRoom_Id(roomId)
				.ifPresent(timeVoteRoom -> {
					timeVoteRepository.deleteAllByTimeVoteRoom(timeVoteRoom);
					meetingDateRepository.deleteAllByTimeVoteRoom(timeVoteRoom);
					timeVoteRoomRepository.delete(timeVoteRoom);
				});
	}
}
