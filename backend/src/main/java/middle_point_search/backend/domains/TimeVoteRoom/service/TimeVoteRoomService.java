package middle_point_search.backend.domains.TimeVoteRoom.service;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.AlreadyVotedException;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.common.exception.DuplicateVoteRoomException;
import middle_point_search.backend.common.exception.errorCode.CommonErrorCode;
import middle_point_search.backend.common.exception.errorCode.UserErrorCode;
import middle_point_search.backend.common.util.MemberLoader;
import middle_point_search.backend.domains.TimeVoteRoom.domain.MeetingDate;
import middle_point_search.backend.domains.TimeVoteRoom.domain.TimeVote;
import middle_point_search.backend.domains.TimeVoteRoom.domain.TimeVoteRoom;
import middle_point_search.backend.domains.TimeVoteRoom.dto.TimeVoteRoomDTO;
import middle_point_search.backend.domains.TimeVoteRoom.repository.MeetingDateRepository;
import middle_point_search.backend.domains.TimeVoteRoom.repository.TimeVoteRepository;
import middle_point_search.backend.domains.TimeVoteRoom.repository.TimeVoteRoomRepository;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static middle_point_search.backend.domains.TimeVoteRoom.dto.TimeVoteRoomDTO.*;
import static middle_point_search.backend.domains.TimeVoteRoom.dto.TimeVoteRoomDTO.TimeVoteRoomCreateRequest;
import static middle_point_search.backend.domains.TimeVoteRoom.dto.TimeVoteRoomDTO.TimeVoteRoomCreateResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimeVoteRoomService {

    private final TimeVoteRoomRepository timeVoteRoomRepository;
    private final TimeVoteRepository timeVoteRepository;
    private final RoomRepository roomRepository;
    private final MeetingDateRepository meetingDateRepository;
    private final MemberLoader memberLoader;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    //시간 투표방 생성
    @Transactional
    public TimeVoteRoomCreateResponse createTimeVoteRoom(TimeVoteRoomCreateRequest request) {
        Member member = memberLoader.getMember();
        String roomId = member.getRoom().getIdentityNumber();
        Room room = roomRepository.findByIdentityNumber(roomId).orElseThrow(() -> new CustomException(CommonErrorCode.INVALID_PARAMETER));

        boolean exists = timeVoteRoomRepository.existsByRoom(room);
        if (exists) {
            throw new DuplicateVoteRoomException();
        }

        TimeVoteRoom timeVoteRoom = new TimeVoteRoom(room);
        timeVoteRoom.setMeetingDates(request.getDates());

        // 시간투표방 저장하여 ID 생성
        TimeVoteRoom savedTimeVoteRoom = timeVoteRoomRepository.save(timeVoteRoom);
        // URL 생성 및 설정
        return TimeVoteRoomCreateResponse.from(savedTimeVoteRoom.getId());
    }


    //시간투표하기
    @Transactional
    public void vote(TimeVoteRoomVoteRequest request) {
        Member member = memberLoader.getMember();
        String roomId = member.getRoom().getIdentityNumber();
        Room room = roomRepository.findRoomByIdentityNumber(roomId).orElseThrow(() -> new CustomException(CommonErrorCode.INVALID_PARAMETER));

        TimeVoteRoom timeVoteRoom = timeVoteRoomRepository.findByRoom_IdentityNumber(roomId)
                .orElseThrow(() -> new CustomException(UserErrorCode.VOTE_ROOM_NOT_FOUND));

        boolean alreadyVoted = timeVoteRepository.existsByTimeVoteRoomAndMember(timeVoteRoom, member);
        if (alreadyVoted) {
            throw new AlreadyVotedException();
        }
        List<TimeVote> timeVotes = new ArrayList<>();
        for (List<String> timeRange : request.getDateTime()) {
            LocalDateTime memberAvailableStartTime = LocalDateTime.parse(timeRange.get(0), formatter);
            LocalDateTime memberAvailableEndTime = LocalDateTime.parse(timeRange.get(1), formatter);
            Optional<MeetingDate> meetingDateOpt = meetingDateRepository.findByTimeVoteRoomAndDate(timeVoteRoom, memberAvailableStartTime.toLocalDate());
            MeetingDate meetingDate = meetingDateOpt.orElseThrow(() -> new CustomException(CommonErrorCode.INVALID_PARAMETER));
            TimeVote timeVote = new TimeVote(timeVoteRoom, meetingDate, member, memberAvailableStartTime, memberAvailableEndTime);
            timeVotes.add(timeVote);
        }

        timeVoteRepository.saveAll(timeVotes);
    }

    // 시간 투표 수정
    @Transactional
    public void updateVote(TimeVoteRoomVoteRequest request) {
        Member member = memberLoader.getMember();
        String roomId = member.getRoom().getIdentityNumber();
        Room room = roomRepository.findByIdentityNumber(roomId).orElseThrow(() -> new CustomException(CommonErrorCode.INVALID_PARAMETER));

        TimeVoteRoom timeVoteRoom = timeVoteRoomRepository.findByRoom_IdentityNumber(roomId)
                .orElseThrow(() -> new CustomException(UserErrorCode.VOTE_ROOM_NOT_FOUND));

        List<TimeVote> existingVotes = timeVoteRepository.findByTimeVoteRoomAndMember(timeVoteRoom, member);
        if (existingVotes.isEmpty()) {
            throw new CustomException(UserErrorCode.VOTE_NOT_FOUND); // 투표를 한적이 없다는걸 나타냄
        }

        // 기존 투표 삭제
        timeVoteRepository.deleteAll(existingVotes);

        // 새로운 투표 추가
        List<TimeVote> timeVotes = new ArrayList<>();
        for (List<String> timeRange : request.getDateTime()) {
            LocalDateTime memberAvailableStartTime = LocalDateTime.parse(timeRange.get(0), formatter);
            LocalDateTime memberAvailableEndTime = LocalDateTime.parse(timeRange.get(1), formatter);

            Optional<MeetingDate> meetingDateOpt = meetingDateRepository.findByTimeVoteRoomAndDate(timeVoteRoom, memberAvailableStartTime.toLocalDate());
            MeetingDate meetingDate = meetingDateOpt.orElseThrow(() -> new CustomException(CommonErrorCode.INVALID_PARAMETER));

            TimeVote timeVote = new TimeVote(timeVoteRoom, meetingDate, member, memberAvailableStartTime, memberAvailableEndTime);
            timeVotes.add(timeVote);
        }

        timeVoteRepository.saveAll(timeVotes);
    }

    //투표나 수정 로직에서 투표방, 투표현황체크하지만 쓰일 경우 대비
    public boolean hasTimeVoteRoom() {
        Member member = memberLoader.getMember();
        String roomId = member.getRoom().getIdentityNumber();
        Room room = roomRepository.findByIdentityNumber(roomId).orElseThrow(() -> new CustomException(CommonErrorCode.INVALID_PARAMETER));

        return timeVoteRoomRepository.existsByRoom(room);
    }

    //투표방이 없다면 투표방없다고 메세지, 있을때 투표 true, 투표안했을때 false
    public boolean hasVoted() {
        Member member = memberLoader.getMember();
        String roomId = member.getRoom().getIdentityNumber();
        Room room = roomRepository.findByIdentityNumber(roomId).orElseThrow(() -> new CustomException(CommonErrorCode.INVALID_PARAMETER));

        TimeVoteRoom timeVoteRoom = timeVoteRoomRepository.findByRoom_IdentityNumber(roomId)
                .orElseThrow(() -> new CustomException(UserErrorCode.VOTE_ROOM_NOT_FOUND));

        return timeVoteRepository.existsByTimeVoteRoomAndMember(timeVoteRoom, member);
    }

    // 시간 투표 현황 정보 조회
    public TimeVoteRoomResultResponse getTimeVoteResult() {
        Member member = memberLoader.getMember();
        String roomId = member.getRoom().getIdentityNumber();
        Room room = roomRepository.findByIdentityNumber(roomId).orElseThrow(() -> new CustomException(CommonErrorCode.INVALID_PARAMETER));

        TimeVoteRoom timeVoteRoom = timeVoteRoomRepository.findByRoom_IdentityNumber(roomId)
                .orElseThrow(() -> new CustomException(UserErrorCode.VOTE_ROOM_NOT_FOUND));

        List<MeetingDate> meetingDates = meetingDateRepository.findByTimeVoteRoom(timeVoteRoom);

        Map<String, List<TimeVoteDetail>> result = new LinkedHashMap<>();
        meetingDates.sort(Comparator.comparing(MeetingDate::getDate));

        for (MeetingDate meetingDate : meetingDates) {
            String date = meetingDate.getDate().toString();
            List<TimeVoteDetail> details = new ArrayList<>();

            // 해당 날짜의 모든 투표 정보 가져오기
            List<TimeVote> timeVotes = timeVoteRepository.findByTimeVoteRoomAndMeetingDate(timeVoteRoom, meetingDate);
            for (TimeVote vote : timeVotes) {
                TimeVoteDetail detail = TimeVoteDetail.from(
                        vote.getMember().getName(),
                        Arrays.asList(vote.getMemberAvailableStartTime().format(formatter), vote.getMemberAvailableEndTime().format(formatter))
                );
                details.add(detail);
            }
            result.put(date, details);
        }
        return TimeVoteRoomResultResponse.from(result);
    }
}
