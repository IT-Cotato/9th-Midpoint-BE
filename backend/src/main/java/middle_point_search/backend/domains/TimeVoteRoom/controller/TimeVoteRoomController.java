package middle_point_search.backend.domains.TimeVoteRoom.controller;

import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(
            summary = "시간투표방 생성하기",
            description = "날짜(yyyy-mm-dd)를 리스트로 입력을 받아서 시간투표방을 생성한다."
    )
    public ResponseEntity<DataResponse<TimeVoteRoomCreateResponse>> timeVoteRoomCreate(@RequestBody TimeVoteRoomCreateRequest request) {
        TimeVoteRoomCreateResponse response = timeVoteRoomService.createTimeVoteRoom(request);
        return ResponseEntity.ok(DataResponse.from(response));
    }

    //투표방 재생성
    @PutMapping
    @Operation(
            summary = "시간투표방 재생성하기",
            description = "날짜(yyyy-mm-dd)를 리스트로 입력을 받아서 시간투표방을 재생성한다."
    )
    public ResponseEntity<DataResponse<TimeVoteRoomCreateResponse>> timeVoteRoomRecreate(@RequestBody TimeVoteRoomCreateRequest request) {
        TimeVoteRoomCreateResponse response = timeVoteRoomService.recreateTimeVoteRoom(request);
        return ResponseEntity.ok(DataResponse.from(response));
    }

    //투표
    @PostMapping("/vote")
    @Operation(
            summary = "시간투표하기",
            description = "가능한 투표후보날짜의 가능한 시작일시(yyyy-mm-dd hh:mm), 가능한 마지막일시(yyyy-mm-dd hh:mm)를 입력을 받아서 투표한다."
    )
    public ResponseEntity<?> vote(@RequestBody TimeVoteRoomVoteRequest request) {
        timeVoteRoomService.vote(request);
        return ResponseEntity.ok(BaseResponse.ok());
    }

    //재투표,투표 수정
    @PutMapping("/vote")
    @Operation(
            summary = "시간 재투표하기",
            description = "가능한 투표후보날짜의 가능한 시작일시(yyyy-mm-dd hh:mm), 가능한 마지막일시(yyyy-mm-dd hh:mm)를 입력을 받아서 재투표한다."
    )
    public ResponseEntity<?> voteUpdate(@RequestBody TimeVoteRoomVoteRequest request) {
        timeVoteRoomService.updateVote(request);
        return ResponseEntity.ok(BaseResponse.ok());
    }

    //투표방존재확인
    @GetMapping("/existence")
    @Operation(
            summary = "시간투표방 존재여부 확인하기",
            description = "시간투표방 존재여부를 나타내고 존재하면 true, 존재하지않으면 false를 반환한다."
    )
    public ResponseEntity<DataResponse<Boolean>> timeVoteRoomHas() {
        boolean exists = timeVoteRoomService.hasTimeVoteRoom();
        return ResponseEntity.ok(DataResponse.from(exists));
    }

    //투표여부
    @GetMapping("/voted")
    @Operation(
            summary = "시간투표여부 확인하기",
            description = "시간투표여부를 나타내고 투표를 했으면 true, 투표를 하지않았으면 false를 반환한다."
    )
    public ResponseEntity<DataResponse<Boolean>> votedHas() {
        boolean hasVoted = timeVoteRoomService.hasVoted();
        return ResponseEntity.ok(DataResponse.from(hasVoted));
    }

    //투표 결과
    @GetMapping("/result")
    @Operation(
            summary = "시간투표결과 확인하기",
            description = "시간투표후보날짜들에 대한 결과를 보여준다. 각 날짜에 대한 멤버들의 투표 현황과 총 투표한 인원의 정보를 반환한다."
    )
    public ResponseEntity<DataResponse<TimeVoteRoomResultResponse>> timeVoteResultsGet() {
        TimeVoteRoomResultResponse result = timeVoteRoomService.getTimeVoteResult();
        return ResponseEntity.ok(DataResponse.from(result));
    }

}
