package middle_point_search.backend.domains.PlaceVoteRoom.controller;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.dto.BaseResponse;
import middle_point_search.backend.common.dto.DataResponse;
import middle_point_search.backend.common.dto.ErrorResponse;
import middle_point_search.backend.common.exception.AlreadyVotedException;
import middle_point_search.backend.common.exception.errorCode.UserErrorCode;
import middle_point_search.backend.domains.PlaceVoteRoom.dto.PlaceVoteRequestDTO;
import middle_point_search.backend.domains.PlaceVoteRoom.dto.PlaceVoteRoomDTO;
import middle_point_search.backend.domains.PlaceVoteRoom.dto.PlaceVoteRoomRequestDTO;
import middle_point_search.backend.domains.PlaceVoteRoom.service.PlaceVoteRoomService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<DataResponse<PlaceVoteRoomCreateResponse>> placeVoteRoomCreate(@RequestBody PlaceVoteRoomRequestDTO request) {
        PlaceVoteRoomCreateResponse response = placeVoteRoomService.createPlaceVoteRoom(request);
        return ResponseEntity.ok(DataResponse.from(response));
    }
    //투표 결과
    @GetMapping("/{placeVoteRoomId}")
    public ResponseEntity<DataResponse<PlaceVoteInfoResponse>> placeVoteRoomGet(@PathVariable Long placeVoteRoomId) {
        PlaceVoteInfoResponse response = placeVoteRoomService.getPlaceVoteRoom(placeVoteRoomId);
        return ResponseEntity.ok(DataResponse.from(response));
    }
    //투표
    @PostMapping("/{place-vote-rooms-id}/vote")
    public ResponseEntity<?> vote(@PathVariable("place-vote-rooms-id") Long placeVoteRoomId, @RequestBody PlaceVoteRequestDTO placeVoteRequest) {
        try {
            placeVoteRoomService.vote(placeVoteRoomId, placeVoteRequest);
            return ResponseEntity.ok(BaseResponse.ok());
        } catch (AlreadyVotedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.from(UserErrorCode.ALREADY_VOTED));
        }
    }
    //재투표
    @PatchMapping("/{place-vote-rooms-id}/vote")
    public ResponseEntity<?> voteUpdate(@PathVariable("place-vote-rooms-id") Long placeVoteRoomId, @RequestBody PlaceVoteRequestDTO placeVoteRequest) {
        placeVoteRoomService.updateVote(placeVoteRoomId, placeVoteRequest);
        return ResponseEntity.ok(BaseResponse.ok());
    }
    //투표방 삭제
    @DeleteMapping("/{placeVoteRoomId}")
    public ResponseEntity<?> placeVoteRoomDelete(@PathVariable Long placeVoteRoomId) {
        placeVoteRoomService.deletePlaceVoteRoom(placeVoteRoomId);
        return ResponseEntity.ok(BaseResponse.ok());
    }
}
