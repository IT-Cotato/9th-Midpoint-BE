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
    @Transactional
    public TimeVoteRoomCreateResponse createTimeVoteRoom(Room room, TimeVoteRoomCreateRequest request) {

        boolean exists = timeVoteRoomRepository.existsByRoom(room);

        //방존재여부 확인
        if (exists) {
            throw new CustomException(DUPLICATE_VOTE_ROOM);
        }

        // 투표 후보가 있는지 확인
        boolean hasNoVoteCandidates = request.getDates() == null || request.getDates().isEmpty();

        if (hasNoVoteCandidates) {
            throw new CustomException(NO_VOTE_CANDIDATES_PROVIDED);
        }

        TimeVoteRoom timeVoteRoom = new TimeVoteRoom(room, request.getDates());
        TimeVoteRoom savedTimeVoteRoom = timeVoteRoomRepository.save(timeVoteRoom);

        return TimeVoteRoomCreateResponse.from(savedTimeVoteRoom.getId());
    }

    //시간투표방 재생성하기
    @Transactional
    public TimeVoteRoomCreateResponse recreateTimeVoteRoom(Room room, TimeVoteRoomCreateRequest request) {

        // 기존 투표방 삭제
        TimeVoteRoom existingTimeVoteRoom = timeVoteRoomRepository.findByRoom(room).orElseThrow(() -> new CustomException(VOTE_ROOM_NOT_FOUND));

        // 먼저 투표와 관련된 모든 데이터 삭제
        timeVoteRoomRepository.delete(existingTimeVoteRoom);
        timeVoteRoomRepository.flush();

        // 투표 후보가 있는지 확인
        boolean hasNoVoteCandidates = request.getDates() == null || request.getDates().isEmpty();

        if (hasNoVoteCandidates) {
            throw new CustomException(NO_VOTE_CANDIDATES_PROVIDED);
        }


        // 새로운 투표방 생성
        TimeVoteRoom timeVoteRoom = new TimeVoteRoom(room, request.getDates());
        TimeVoteRoom savedTimeVoteRoom = timeVoteRoomRepository.save(timeVoteRoom);

        return TimeVoteRoomCreateResponse.from(savedTimeVoteRoom.getId());
    }

    //시간투표하기
    @Transactional
    public void vote(Member member, Room room, TimeVoteRoomVoteRequest request) {

        TimeVoteRoom timeVoteRoom = timeVoteRoomRepository.findByRoom(room).orElseThrow(() -> new CustomException(VOTE_ROOM_NOT_FOUND));

        boolean alreadyVoted = timeVoteRepository.existsByTimeVoteRoomAndMember(timeVoteRoom, member);

        if (alreadyVoted) {
            throw new CustomException(ALREADY_VOTED);
        }
        List<TimeVote> timeVotes = createNewTimeVotes(request.getDateTime(), timeVoteRoom, member);
        timeVoteRepository.saveAll(timeVotes);
    }

    // 시간 투표 수정
    @Transactional
    public void updateVote(Member member, Room room, TimeVoteRoomVoteRequest request) {

        TimeVoteRoom timeVoteRoom = timeVoteRoomRepository.findByRoom(room).orElseThrow(() -> new CustomException(VOTE_ROOM_NOT_FOUND));

        List<TimeVote> existingVotes = timeVoteRepository.findByTimeVoteRoomAndMember(timeVoteRoom, member);
        if (existingVotes.isEmpty()) {
            throw new CustomException(VOTE_NOT_FOUND);
        }
        // 기존 투표 삭제
        timeVoteRepository.deleteAll(existingVotes);
        // 새로운 투표 추가
        List<TimeVote> timeVotes = createNewTimeVotes(request.getDateTime(), timeVoteRoom, member);
        timeVoteRepository.saveAll(timeVotes);
    }

    private List<TimeVote> createNewTimeVotes(List<TimeRange> dateTimeRanges, TimeVoteRoom timeVoteRoom, Member member) {
        List<TimeVote> timeVotes = new ArrayList<>();
        for (TimeRange timeRange : dateTimeRanges) {
            LocalDateTime memberAvailableStartTime = timeRange.getMemberAvailableStartTime();
            LocalDateTime memberAvailableEndTime = timeRange.getMemberAvailableEndTime();
            LocalDateTime startDateTime = memberAvailableStartTime.toLocalDate().atStartOfDay();
            Optional<MeetingDate> meetingDateOpt = meetingDateRepository.findByTimeVoteRoomAndDate(timeVoteRoom, startDateTime.toLocalDate());
            MeetingDate meetingDate = meetingDateOpt.orElseThrow(() -> new CustomException(VOTE_ROOM_NOT_FOUND));
            TimeVote timeVote = new TimeVote(timeVoteRoom, meetingDate, member, memberAvailableStartTime, memberAvailableEndTime);
            timeVotes.add(timeVote);
        }
        return timeVotes;
    }
    //투표나 수정 로직에서 투표방, 투표현황체크하지만 쓰일 경우 대비
    public boolean hasTimeVoteRoom(String roomId) {

        return timeVoteRoomRepository.existsByRoomIdentityNumber(roomId);
    }

    //투표방이 없다면 투표방없다고 메세지, 있을때 투표 true, 투표안했을때 false
    public boolean hasVoted(Member member, Room room) {

        TimeVoteRoom timeVoteRoom = timeVoteRoomRepository.findByRoom(room).orElseThrow(() -> new CustomException(VOTE_ROOM_NOT_FOUND));

        return timeVoteRepository.existsByTimeVoteRoomAndMember(timeVoteRoom, member);
    }

    // 시간 투표 현황 정보 조회
    public TimeVoteRoomResultResponse getTimeVoteResult(Room room) {
        TimeVoteRoom timeVoteRoom = timeVoteRoomRepository.findByRoom(room).orElseThrow(() -> new CustomException(VOTE_ROOM_NOT_FOUND));
        List<MeetingDate> meetingDates = meetingDateRepository.findByTimeVoteRoom(timeVoteRoom);

        Map<String, List<TimeVoteDetail>> result = new LinkedHashMap<>();
        meetingDates.sort(Comparator.comparing(MeetingDate::getDate));

        for (MeetingDate meetingDate : meetingDates) {
            String date = meetingDate.getDate().toString();
            List<TimeVoteDetail> details = new ArrayList<>();

            // 해당 날짜의 모든 투표 정보 가져오기
            List<TimeVote> timeVotes = timeVoteRepository.findByTimeVoteRoomAndMeetingDate(timeVoteRoom, meetingDate);
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
        int totalMemberNum = (int) distinctVotes.stream().map(TimeVote::getMember).distinct().count();
        return TimeVoteRoomResultResponse.from(result, totalMemberNum);
    }
}
