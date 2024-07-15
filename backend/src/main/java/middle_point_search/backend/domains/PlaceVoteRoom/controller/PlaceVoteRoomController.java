package middle_point_search.backend.domains.PlaceVoteRoom.controller;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.dto.BaseResponse;
import middle_point_search.backend.common.dto.DataResponse;
import middle_point_search.backend.domains.PlaceVoteRoom.service.PlaceVoteRoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static middle_point_search.backend.domains.PlaceVoteRoom.dto.PlaceVoteRoomDTO.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/place-vote-rooms")
public class PlaceVoteRoomController {

    private final PlaceVoteRoomService placeVoteRoomService;

    //투표방 생성
    @PostMapping
    public ResponseEntity<DataResponse<PlaceVoteRoomCreateResponse>> placeVoteRoomCreate(@RequestBody PlaceVoteRoomCreateRequest request) {
        PlaceVoteRoomCreateResponse response = placeVoteRoomService.createPlaceVoteRoom(request);
        return ResponseEntity.ok(DataResponse.from(response));
    }

    @PutMapping
    public ResponseEntity<DataResponse<PlaceVoteRoomCreateResponse>> placeVoteRoomRecreate(@RequestBody PlaceVoteRoomCreateRequest request) {
        PlaceVoteRoomCreateResponse response = placeVoteRoomService.recreatePlaceVoteRoom(request);
        return ResponseEntity.ok(DataResponse.from(response));
    }

    //투표 결과
    @GetMapping("/result")
    public ResponseEntity<DataResponse<PlaceVoteInfoResponse>> placeVoteRoomGet() {
        PlaceVoteInfoResponse response = placeVoteRoomService.getPlaceVoteRoom();
        return ResponseEntity.ok(DataResponse.from(response));
    }

    //투표
    @PostMapping("/vote")
    public ResponseEntity<?> vote(@RequestBody PlaceVoteRequest request) {
        placeVoteRoomService.vote(request);
        return ResponseEntity.ok(BaseResponse.ok());
    }

    //재투표
    @PutMapping("/vote")
    public ResponseEntity<?> voteUpdate(@RequestBody PlaceVoteRequest request) {
        placeVoteRoomService.updateVote(request);
        return ResponseEntity.ok(BaseResponse.ok());
    }

    //투표방 존재 확인
    @GetMapping("/existence")
    public ResponseEntity<DataResponse<Boolean>> placeVoteRoomHas() {
        boolean exists = placeVoteRoomService.hasPlaceVoteRoom();
        return ResponseEntity.ok(DataResponse.from(exists));
    }

    //투표여부
    @GetMapping("/voted")
    public ResponseEntity<DataResponse<Boolean>> votedHas() {
        boolean hasVoted = placeVoteRoomService.hasVoted();
        return ResponseEntity.ok(DataResponse.from(hasVoted));
    }
}
