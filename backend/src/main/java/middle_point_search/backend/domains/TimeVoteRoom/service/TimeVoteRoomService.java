package middle_point_search.backend.domains.TimeVoteRoom.service;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.common.exception.DuplicateVoteRoomException;
import middle_point_search.backend.common.exception.errorCode.CommonErrorCode;
import middle_point_search.backend.common.util.MemberLoader;
import middle_point_search.backend.domains.TimeVoteRoom.domain.TimeVoteRoom;
import middle_point_search.backend.domains.TimeVoteRoom.repository.TimeVoteRoomRepository;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static middle_point_search.backend.domains.TimeVoteRoom.dto.TimeVoteRoomDTO.TimeVoteRoomCreateRequest;
import static middle_point_search.backend.domains.TimeVoteRoom.dto.TimeVoteRoomDTO.TimeVoteRoomCreateResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimeVoteRoomService {

    private final TimeVoteRoomRepository timeVoteRoomRepository;
    private final RoomRepository roomRepository;
    private final MemberLoader memberLoader;

    //시간 투표방 생성
    @Transactional
    public TimeVoteRoomCreateResponse createTimeVoteRoom(TimeVoteRoomCreateRequest request) {
        Room room = roomRepository.findByIdentityNumber(request.getRoomId()).orElseThrow(() -> new CustomException(CommonErrorCode.INVALID_PARAMETER));

        boolean exists = timeVoteRoomRepository.existsByRoom(room);
        if (exists) {
            throw new DuplicateVoteRoomException();
        }

        TimeVoteRoom timeVoteRoom = new TimeVoteRoom(room, request.getMeetingName());
        timeVoteRoom.setMeetingDates(request.getDates());

        // 시간투표방 저장하여 ID 생성
        TimeVoteRoom savedTimeVoteRoom = timeVoteRoomRepository.save(timeVoteRoom);
        // URL 생성 및 설정
        String votePageUrl = generateVotePageUrl(savedTimeVoteRoom);
        savedTimeVoteRoom.setUrl(votePageUrl);

        return TimeVoteRoomCreateResponse.from(savedTimeVoteRoom.getId(), savedTimeVoteRoom.getUrl());
    }

    private String generateVotePageUrl(TimeVoteRoom timeVoteRoom) {
        // URL 생성 로직 (예: 특정 패턴에 따라 URL 생성)
        return "http://api/place-vote-room/" + timeVoteRoom.getId();
    }
}