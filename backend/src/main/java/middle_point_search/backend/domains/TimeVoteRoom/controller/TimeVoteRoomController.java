package middle_point_search.backend.domains.TimeVoteRoom.controller;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.dto.BaseResponse;
import middle_point_search.backend.common.dto.DataResponse;
import middle_point_search.backend.domains.TimeVoteRoom.service.TimeVoteRoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static middle_point_search.backend.domains.TimeVoteRoom.dto.TimeVoteRoomDTO.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/time-vote-rooms")
public class TimeVoteRoomController {
    private final TimeVoteRoomService timeVoteRoomService;

    //투표방 생성
    @PostMapping
    public ResponseEntity<DataResponse<TimeVoteRoomCreateResponse>> timeVoteRoomCreate(@RequestBody TimeVoteRoomCreateRequest request) {
        TimeVoteRoomCreateResponse response = timeVoteRoomService.createTimeVoteRoom(request);
        return ResponseEntity.ok(DataResponse.from(response));
    }

    //투표방 재생성
    @PutMapping
    public ResponseEntity<DataResponse<TimeVoteRoomCreateResponse>> timeVoteRoomRecreate(@RequestBody TimeVoteRoomCreateRequest request) {
        TimeVoteRoomCreateResponse response = timeVoteRoomService.recreateTimeVoteRoom(request);
        return ResponseEntity.ok(DataResponse.from(response));
    }

    //투표
    @PostMapping("/vote")
    public ResponseEntity<?> vote(@RequestBody TimeVoteRoomVoteRequest request) {
        timeVoteRoomService.vote(request);
        return ResponseEntity.ok(BaseResponse.ok());
    }

    //재투표,투표 수정
    @PutMapping("/vote")
    public ResponseEntity<?> voteUpdate(@RequestBody TimeVoteRoomVoteRequest request) {
        timeVoteRoomService.updateVote(request);
        return ResponseEntity.ok(BaseResponse.ok());
    }

    //투표방존재확인
    @GetMapping("/existence")
    public ResponseEntity<DataResponse<Boolean>> timeVoteRoomHas() {
        boolean exists = timeVoteRoomService.hasTimeVoteRoom();
        return ResponseEntity.ok(DataResponse.from(exists));
    }

    //투표여부
    @GetMapping("/voted")
    public ResponseEntity<DataResponse<Boolean>> votedHas() {
        boolean hasVoted = timeVoteRoomService.hasVoted();
        return ResponseEntity.ok(DataResponse.from(hasVoted));
    }

    //투표 결과
    @GetMapping("/result")
    public ResponseEntity<DataResponse<TimeVoteRoomResultResponse>> timeVoteResultsGet() {
        TimeVoteRoomResultResponse result = timeVoteRoomService.getTimeVoteResult();
        return ResponseEntity.ok(DataResponse.from(result));
    }

}
