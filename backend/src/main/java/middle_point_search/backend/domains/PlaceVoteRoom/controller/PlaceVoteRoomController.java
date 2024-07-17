package middle_point_search.backend.domains.PlaceVoteRoom.controller;

import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(
            summary = "장소투표방 생성하기",
            description = "장소후보를 리스트로 입력을 받아서 장소투표방을 생성한다."
    )
    public ResponseEntity<DataResponse<PlaceVoteRoomCreateResponse>> placeVoteRoomCreate(@RequestBody PlaceVoteRoomCreateRequest request) {
        PlaceVoteRoomCreateResponse response = placeVoteRoomService.createPlaceVoteRoom(request);
        return ResponseEntity.ok(DataResponse.from(response));
    }

    @PutMapping
    @Operation(
            summary = "장소투표방 재생성하기",
            description = "장소후보를 리스트로 입력을 받아서 장소투표방을 재생성한다."
    )
    public ResponseEntity<DataResponse<PlaceVoteRoomCreateResponse>> placeVoteRoomRecreate(@RequestBody PlaceVoteRoomCreateRequest request) {
        PlaceVoteRoomCreateResponse response = placeVoteRoomService.recreatePlaceVoteRoom(request);
        return ResponseEntity.ok(DataResponse.from(response));
    }

    //투표 결과
    @GetMapping("/result")
    @Operation(
            summary = "장소투표방 결과 조회하기",
            description = "각 장소후보별로 해당하는 장소투표후보 id, 장소투표후보이름, 투표수, 투표한 멤버 id(이름)리스트를 반환한다."
    )
    public ResponseEntity<DataResponse<PlaceVoteInfoResponse>> placeVoteRoomGet() {
        PlaceVoteInfoResponse response = placeVoteRoomService.getPlaceVoteRoom();
        return ResponseEntity.ok(DataResponse.from(response));
    }

    //투표
    @PostMapping("/vote")
    @Operation(
            summary = "장소투표하기",
            description = "장소투표후보 id를 응답받아 해당하는 id를 가진 투표후보를 투표하도록 한다"
    )
    public ResponseEntity<?> vote(@RequestBody PlaceVoteRequest request) {
        placeVoteRoomService.vote(request);
        return ResponseEntity.ok(BaseResponse.ok());
    }

    //재투표
    @PutMapping("/vote")
    @Operation(
            summary = "장소 재투표하기",
            description = "장소투표후보 id를 응답받아 해당하는 id를 가진 투표후보를 재투표하도록 한다"
    )
    public ResponseEntity<?> voteUpdate(@RequestBody PlaceVoteRequest request) {
        placeVoteRoomService.updateVote(request);
        return ResponseEntity.ok(BaseResponse.ok());
    }

    //투표방 존재 확인
    @GetMapping("/existence")
    @Operation(
            summary = "장소투표방 존재여부 확인하기",
            description = "장소투표방 존재여부를 나타내고 존재하면 true, 존재하지않으면 false를 반환한다."
    )
    public ResponseEntity<DataResponse<Boolean>> placeVoteRoomHas() {
        boolean exists = placeVoteRoomService.hasPlaceVoteRoom();
        return ResponseEntity.ok(DataResponse.from(exists));
    }

    //투표여부
    @GetMapping("/voted")
    @Operation(
            summary = "장소투표여부 확인하기",
            description = "장소투표여부를 나타내고 투표를 했으면 true, 투표를 하지않았으면 false를 반환한다."
    )
    public ResponseEntity<DataResponse<Boolean>> votedHas() {
        boolean hasVoted = placeVoteRoomService.hasVoted();
        return ResponseEntity.ok(DataResponse.from(hasVoted));
    }
}
