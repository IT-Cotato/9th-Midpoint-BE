package middle_point_search.backend.domains.TimeVoteRoom.controller;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.dto.BaseResponse;
import middle_point_search.backend.common.dto.DataResponse;
import middle_point_search.backend.common.dto.ErrorResponse;
import middle_point_search.backend.common.exception.AlreadyVotedException;
import middle_point_search.backend.common.exception.errorCode.UserErrorCode;
import middle_point_search.backend.domains.PlaceVoteRoom.dto.PlaceVoteRequestDTO;
import middle_point_search.backend.domains.TimeVoteRoom.dto.TimeVoteRoomDTO;
import middle_point_search.backend.domains.TimeVoteRoom.service.TimeVoteRoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static middle_point_search.backend.domains.TimeVoteRoom.dto.TimeVoteRoomDTO.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/time-vote-rooms")
public class TimeVoteRoomController {
    private final TimeVoteRoomService timeVoteRoomService;

    @PostMapping
    public ResponseEntity<DataResponse<TimeVoteRoomCreateResponse>> timeVoteRoomCreate(@RequestBody TimeVoteRoomCreateRequest request) {
        TimeVoteRoomCreateResponse response = timeVoteRoomService.createTimeVoteRoom(request);
        return ResponseEntity.ok(DataResponse.from(response));
    }

    @PostMapping("/vote")
    public ResponseEntity<?> vote(@RequestBody TimeVoteRoomVoteRequest request) {
        timeVoteRoomService.vote(request);
        return ResponseEntity.ok(BaseResponse.ok());
    }
}
