package middle_point_search.backend.domains.timeVoteRoom.service;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.timeVoteRoom.domain.MeetingDate;
import middle_point_search.backend.domains.timeVoteRoom.domain.TimeVote;
import middle_point_search.backend.domains.timeVoteRoom.domain.TimeVoteRoom;
import middle_point_search.backend.domains.timeVoteRoom.dto.TimeVoteDTO.TimeRange;
import middle_point_search.backend.domains.timeVoteRoom.dto.TimeVoteDTO.TimeVoteDetail;
import middle_point_search.backend.domains.timeVoteRoom.dto.TimeVoteDTO.TimeVotePerDate;
import middle_point_search.backend.domains.timeVoteRoom.dto.TimeVoteDTO.TimeVoteRoomResultResponse;
import middle_point_search.backend.domains.timeVoteRoom.dto.TimeVoteDTO.VoteRequest;
import middle_point_search.backend.domains.timeVoteRoom.dto.TimeVoteDTO.VotedAndVoteItemsGetResponse;
import middle_point_search.backend.domains.timeVoteRoom.repository.TimeVoteRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimeVoteService {

	private final TimeVoteRepository timeVoteRepository;
	private final MeetingDateService meetingDateService;
	private final TimeVoteRoomService timeVoteRoomService;

	//시간투표하기
	@Transactional(rollbackFor = {CustomException.class})
	public void vote(
		Member member,
		Long roomId,
		VoteRequest request
	) {
		TimeVoteRoom timeVoteRoom = timeVoteRoomService.findByRoomId(roomId)
			.orElseThrow(() -> CustomException.from(VOTE_ROOM_NOT_FOUND));

		// 이미 투표했는지 확인
		boolean alreadyVoted = timeVoteRepository.existsByTimeVoteRoomAndMember(timeVoteRoom, member);
		if (alreadyVoted) {
			throw CustomException.from(ALREADY_VOTED);
		}

		// 새로운 투표 추가
		List<TimeVote> timeVotes = request.getDateTime().stream()
			.map(dateTime -> createTimeVote(dateTime, timeVoteRoom, member))
			.toList();

		timeVoteRepository.saveAll(timeVotes);
	}

	// 시간 투표 수정
	@Transactional(rollbackFor = {CustomException.class})
	public void updateVote(
		Member member,
		Long roomId,
		VoteRequest request
	) {
		TimeVoteRoom timeVoteRoom = timeVoteRoomService.findByRoomId(roomId)
			.orElseThrow(() -> CustomException.from(VOTE_ROOM_NOT_FOUND));

		List<TimeVote> existingVotes = timeVoteRepository.findAllByTimeVoteRoomAndMember(timeVoteRoom, member);

		// 투표가 없으면 예외 발생
		if (existingVotes.isEmpty()) {
			throw CustomException.from(VOTE_NOT_FOUND);
		}

		// 기존 투표 삭제
		timeVoteRepository.deleteAll(existingVotes);

		// 새로운 투표 추가
		List<TimeVote> timeVotes = request.getDateTime().stream()
			.map(dateTime -> createTimeVote(dateTime, timeVoteRoom, member))
			.toList();

		timeVoteRepository.saveAll(timeVotes);
	}

	// TimeVote 엔티티 생성
	private TimeVote createTimeVote(
		TimeRange dateTime,
		TimeVoteRoom timeVoteRoom,
		Member member
	) {
		LocalDateTime memberAvailableStartTime = dateTime.getMemberAvailableStartTime();
		LocalDateTime memberAvailableEndTime = dateTime.getMemberAvailableEndTime();
		LocalDateTime startDateTime = memberAvailableStartTime.toLocalDate().atStartOfDay();

		MeetingDate meetingDate = meetingDateService.findByTimeVoteRoomAndDate(
				timeVoteRoom,
				startDateTime.toLocalDate())
			.orElseThrow(() -> CustomException.from(CANDIDATE_NOT_FOUND));

		return new TimeVote(
			timeVoteRoom,
			meetingDate,
			member,
			memberAvailableStartTime,
			memberAvailableEndTime);
	}

	// 시간 투표 현황 정보 조회
	public TimeVoteRoomResultResponse findTimeVoteResult(Long roomId) {
		TimeVoteRoom timeVoteRoom = timeVoteRoomService.findByRoomId(roomId)
			.orElseThrow(() -> CustomException.from(VOTE_ROOM_NOT_FOUND));

		// 모든 투표 정보 가져오기
		List<MeetingDate> meetingDates = meetingDateService.findByTimeVoteRoom(timeVoteRoom);

		Map<String, List<TimeVoteDetail>> result = new LinkedHashMap<>();
		meetingDates.sort(Comparator.comparing(MeetingDate::getDate));

		for (MeetingDate meetingDate : meetingDates) {
			String date = meetingDate.getDate().toString();
			List<TimeVoteDetail> details = new ArrayList<>();

			// 해당 날짜의 모든 투표 정보 가져오기
			List<TimeVote> timeVotes = timeVoteRepository.findAllByTimeVoteRoomAndMeetingDate(timeVoteRoom,
				meetingDate);
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

	// 내 시간투표 가져오기
	private List<TimeRange> findMyVotes(TimeVoteRoom timeVoteRoom, Member member) {
		List<TimeVote> myTimeVotes = timeVoteRepository.findAllByTimeVoteRoomAndMember(timeVoteRoom,
			member);

		return myTimeVotes.stream()
			.map(myTimeVote -> new TimeRange(myTimeVote.getMemberAvailableStartTime(),
				myTimeVote.getMemberAvailableEndTime()))
			.toList();
	}

	// 다른 사람 시간 투표 가져오기
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
				.sorted(Comparator.comparing(detail -> detail.getDateTime().getMemberAvailableStartTime()))
				.toList();

			TimeVotePerDate timeVotePerDate = TimeVotePerDate.from(meetingDate.getDate(), timeVotePerDateDetails);

			otherVotes.add(timeVotePerDate);
		}

		return otherVotes;
	}

	// 투표 여부 및 투표 아이템 가져오기
	public VotedAndVoteItemsGetResponse getVotedAndVoteItems(Member member, Long roomId) {
		TimeVoteRoom timeVoteRoom = timeVoteRoomService.findByRoomId(roomId)
			.orElseThrow(() -> CustomException.from(VOTE_ROOM_NOT_FOUND));

		//내 시간투표 가져오기
		List<TimeRange> myVotes = findMyVotes(timeVoteRoom, member);
		boolean myVoteExistence = !myVotes.isEmpty();
		myVotes = myVoteExistence ? myVotes : null;

		//다른 사람 시간 투표 가져오기
		List<TimeVotePerDate> otherVotes = getOtherVotes(timeVoteRoom, member);
		boolean otherVotesExistence = !otherVotes.isEmpty();
		otherVotes = otherVotesExistence ? otherVotes : null;

		return VotedAndVoteItemsGetResponse.from(myVoteExistence, myVotes, otherVotesExistence, otherVotes);
	}

}
