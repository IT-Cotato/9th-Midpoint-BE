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

    @PostMapping
    public ResponseEntity<DataResponse<PlaceVoteRoomCreateResponse>> placeVoteRoomCreate(@RequestBody PlaceVoteRoomRequestDTO request) {
        PlaceVoteRoomCreateResponse response = placeVoteRoomService.createPlaceVoteRoom(request);
        return ResponseEntity.ok(DataResponse.from(response));
    }
    @GetMapping("/{placeVoteRoomId}")
    public ResponseEntity<DataResponse<PlaceVoteInfoResponse>> placeVoteRoomGet(@PathVariable Long placeVoteRoomId) {
        PlaceVoteInfoResponse response = placeVoteRoomService.getPlaceVoteRoom(placeVoteRoomId);
        return ResponseEntity.ok(DataResponse.from(response));
    }

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
    @PatchMapping("/{place-vote-rooms-id}/vote")
    public ResponseEntity<?> voteUpdate(@PathVariable("place-vote-rooms-id") Long placeVoteRoomId, @RequestBody PlaceVoteRequestDTO placeVoteRequest) {
        placeVoteRoomService.updateVote(placeVoteRoomId, placeVoteRequest);
        return ResponseEntity.ok(BaseResponse.ok());
    }
    @DeleteMapping("/{placeVoteRoomId}")
    public ResponseEntity<?> placeVoteRoomDelete(@PathVariable Long placeVoteRoomId) {
        placeVoteRoomService.deletePlaceVoteRoom(placeVoteRoomId);
        return ResponseEntity.ok(BaseResponse.ok());
    }
}
