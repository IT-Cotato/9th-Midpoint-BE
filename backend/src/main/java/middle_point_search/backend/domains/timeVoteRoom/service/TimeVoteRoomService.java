package middle_point_search.backend.domains.timeVoteRoom.service;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.domains.timeVoteRoom.domain.MeetingDate;
import middle_point_search.backend.domains.timeVoteRoom.domain.TimeVote;
import middle_point_search.backend.domains.timeVoteRoom.domain.TimeVoteRoom;
import middle_point_search.backend.domains.timeVoteRoom.repository.MeetingDateRepository;
import middle_point_search.backend.domains.timeVoteRoom.repository.TimeVoteRepository;
import middle_point_search.backend.domains.timeVoteRoom.repository.TimeVoteRoomRepository;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.room.domain.Room;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;
import static middle_point_search.backend.domains.timeVoteRoom.dto.TimeVoteRoomDTO.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimeVoteRoomService {

	private final TimeVoteRoomRepository timeVoteRoomRepository;
	private final TimeVoteRepository timeVoteRepository;
	private final MeetingDateRepository meetingDateRepository;

	//시간 투표방 생성
	@Transactional(rollbackFor = {CustomException.class})
	public TimeVoteRoomCreateResponse createTimeVoteRoom(Room room, TimeVoteRoomCreateRequest request) {

		boolean exists = timeVoteRoomRepository.existsByRoom(room);

		//방존재여부 확인
		if (exists) {
			throw new CustomException(DUPLICATE_VOTE_ROOM);
		}

		TimeVoteRoom timeVoteRoom = new TimeVoteRoom(room, request.getDates());
		TimeVoteRoom savedTimeVoteRoom = timeVoteRoomRepository.save(timeVoteRoom);

		return TimeVoteRoomCreateResponse.from(savedTimeVoteRoom.getId());
	}

	//시간투표방 재생성하기
	@Transactional(rollbackFor = {CustomException.class})
	public TimeVoteRoomCreateResponse recreateTimeVoteRoom(Room room, TimeVoteRoomCreateRequest request) {

		// 기존 투표방 삭제
		TimeVoteRoom existingTimeVoteRoom = timeVoteRoomRepository.findByRoom(room)
			.orElseThrow(() -> new CustomException(VOTE_ROOM_NOT_FOUND));

		// 먼저 투표와 관련된 모든 데이터 삭제
		timeVoteRoomRepository.delete(existingTimeVoteRoom);
		timeVoteRoomRepository.flush();

		// 새로운 투표방 생성
		TimeVoteRoom timeVoteRoom = new TimeVoteRoom(room, request.getDates());
		TimeVoteRoom savedTimeVoteRoom = timeVoteRoomRepository.save(timeVoteRoom);

		return TimeVoteRoomCreateResponse.from(savedTimeVoteRoom.getId());
	}

	//시간투표하기
	@Transactional(rollbackFor = {CustomException.class})
	public void vote(Member member, Room room, TimeVoteRoomVoteRequest request) {

		TimeVoteRoom timeVoteRoom = timeVoteRoomRepository.findByRoom(room)
			.orElseThrow(() -> new CustomException(VOTE_ROOM_NOT_FOUND));

		boolean alreadyVoted = timeVoteRepository.existsByTimeVoteRoomAndMember(timeVoteRoom, member);

		if (alreadyVoted) {
			throw new CustomException(ALREADY_VOTED);
		}
		List<TimeVote> timeVotes = createNewTimeVotes(request.getDateTime(), timeVoteRoom, member);
		timeVoteRepository.saveAll(timeVotes);
	}

	// 시간 투표 수정
	@Transactional(rollbackFor = {CustomException.class})
	public void updateVote(Member member, Room room, TimeVoteRoomVoteRequest request) {

		TimeVoteRoom timeVoteRoom = timeVoteRoomRepository.findByRoom(room)
			.orElseThrow(() -> new CustomException(VOTE_ROOM_NOT_FOUND));

		List<TimeVote> existingVotes = timeVoteRepository.findAllByTimeVoteRoomAndMember(timeVoteRoom, member);
		if (existingVotes.isEmpty()) {
			throw new CustomException(VOTE_NOT_FOUND);
		}
		// 기존 투표 삭제
		timeVoteRepository.deleteAll(existingVotes);
		// 새로운 투표 추가
		List<TimeVote> timeVotes = createNewTimeVotes(request.getDateTime(), timeVoteRoom, member);
		timeVoteRepository.saveAll(timeVotes);
	}

	private List<TimeVote> createNewTimeVotes(List<TimeRange> dateTimeRanges, TimeVoteRoom timeVoteRoom,
		Member member) {
		List<TimeVote> timeVotes = new ArrayList<>();
		for (TimeRange timeRange : dateTimeRanges) {
			LocalDateTime memberAvailableStartTime = timeRange.getMemberAvailableStartTime();
			LocalDateTime memberAvailableEndTime = timeRange.getMemberAvailableEndTime();
			LocalDateTime startDateTime = memberAvailableStartTime.toLocalDate().atStartOfDay();
			Optional<MeetingDate> meetingDateOpt = meetingDateRepository.findByTimeVoteRoomAndDate(timeVoteRoom,
				startDateTime.toLocalDate());
			MeetingDate meetingDate = meetingDateOpt.orElseThrow(() -> new CustomException(VOTE_ROOM_NOT_FOUND));
			TimeVote timeVote = new TimeVote(timeVoteRoom, meetingDate, member, memberAvailableStartTime,
				memberAvailableEndTime);
			timeVotes.add(timeVote);
		}
		return timeVotes;
	}

	// 시간 투표 현황 정보 조회
	public TimeVoteRoomResultResponse getTimeVoteResult(Room room) {
		TimeVoteRoom timeVoteRoom = timeVoteRoomRepository.findByRoom(room)
			.orElseThrow(() -> new CustomException(VOTE_ROOM_NOT_FOUND));
		List<MeetingDate> meetingDates = meetingDateRepository.findByTimeVoteRoom(timeVoteRoom);

		Map<String, List<TimeVoteDetail>> result = new LinkedHashMap<>();
		meetingDates.sort(Comparator.comparing(MeetingDate::getDate));

		for (MeetingDate meetingDate : meetingDates) {
			String date = meetingDate.getDate().toString();
			List<TimeVoteDetail> details = new ArrayList<>();

			// 해당 날짜의 모든 투표 정보 가져오기
			List<TimeVote> timeVotes = timeVoteRepository.findAllByTimeVoteRoomAndMeetingDate(timeVoteRoom, meetingDate);
			for (TimeVote vote : timeVotes) {
				List<TimeRange> dateTimeList = Arrays.asList(
					new TimeRange(
						vote.getMemberAvailableStartTime(),
						vote.getMemberAvailableEndTime()
					)
				);
				TimeVoteDetail detail = TimeVoteDetail.from(vote.getMember().getName(), dateTimeList);
				details.add(detail);
			}
			result.put(date, details);
		}

		List<TimeVote> distinctVotes = timeVoteRepository.findDistinctByTimeVoteRoom(timeVoteRoom);
		int totalMemberNum = (int)distinctVotes.stream().map(TimeVote::getMember).distinct().count();
		return TimeVoteRoomResultResponse.from(result, totalMemberNum);
	}

	//시간투표방 존재 여부 확인, 존재시 true, 존재하지 않을시 false 반환
	public TimeVoteRoomGetResponse getTimeVoteRoom(String roomId) {

		Optional<TimeVoteRoom> timeVoteRoomOptional = timeVoteRoomRepository.findByRoom_IdentityNumber(roomId);

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

	//먼저 시간투표방이 없다면 시간투표방없다고 에러메세지, 그 다음 시간 투표방이 있을때 투표했으면 true, 투표안했으면 false 반환
	public VotedAndVoteItemsGetResponse getVotedAndVoteItems(Member member, Room room) {

		TimeVoteRoom timeVoteRoom = timeVoteRoomRepository.findByRoom(room)
			.orElseThrow(() -> new CustomException(VOTE_ROOM_NOT_FOUND));

		//내 시간투표 가져오기
		List<TimeRange> myVotes = getMyVotes(timeVoteRoom, member);
		boolean myVoteExistence = !myVotes.isEmpty();
		myVotes = myVoteExistence ? myVotes : null;

		//다른 사람 시간 투표 가져오기
		List<TimeVotePerDate> otherVotes = getOtherVotes(timeVoteRoom, member);
		boolean otherVotesExistence = !otherVotes.isEmpty();
		otherVotes = otherVotesExistence ? otherVotes : null;

		return VotedAndVoteItemsGetResponse.from(myVoteExistence, myVotes, otherVotesExistence, otherVotes);
	}

	private List<TimeRange> getMyVotes(TimeVoteRoom timeVoteRoom, Member member) {
		List<TimeVote> myTimeVotes = timeVoteRepository.findAllByTimeVoteRoomAndMember(timeVoteRoom,
			member);

		return myTimeVotes.stream()
			.map(myTimeVote -> new TimeRange(myTimeVote.getMemberAvailableStartTime(),
				myTimeVote.getMemberAvailableEndTime()))
			.toList();
	}

	private List<TimeVotePerDate> getOtherVotes(TimeVoteRoom timeVoteRoom, Member member) {
		List<TimeVotePerDate> otherVotes = new ArrayList<>();

		List<MeetingDate> meetingDates = timeVoteRoom.getMeetingDates();

		for (MeetingDate meetingDate : meetingDates) {
			List<TimeVotePerDate.TimeVotePerDateDetail> timeVotePerDateDetails = timeVoteRepository.findAllByTimeVoteRoomAndMeetingDateExceptMember(
					timeVoteRoom, meetingDate, member)
				.stream()
				.map(otherTimeVote -> {
					TimeRange timeRange = new TimeRange(otherTimeVote.getMemberAvailableStartTime(),
						otherTimeVote.getMemberAvailableEndTime());
					return TimeVotePerDate.TimeVotePerDateDetail.from(otherTimeVote.getMember().getName(), timeRange);
				})
				.toList();

			TimeVotePerDate timeVotePerDate = TimeVotePerDate.from(meetingDate.getDate(), timeVotePerDateDetails);

			otherVotes.add(timeVotePerDate);
		}

		return otherVotes;
	}
}
