package middle_point_search.backend.domains.PlaceVoteRoom.controller;


import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.dto.DataResponse;
import middle_point_search.backend.domains.PlaceVoteRoom.dto.PlaceVoteRoomDTO;
import middle_point_search.backend.domains.PlaceVoteRoom.dto.PlaceVoteRoomRequestDTO;
import middle_point_search.backend.domains.PlaceVoteRoom.service.PlaceVoteRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/place-vote-rooms")

public class PlaceVoteRoomController {


    @Autowired
    private PlaceVoteRoomService placeVoteRoomService;


    @PostMapping
    public ResponseEntity<DataResponse<PlaceVoteRoomDTO.PlaceVoteRoomCreateResponse>> createPlaceVoteRoom(@RequestBody PlaceVoteRoomRequestDTO request) {
        PlaceVoteRoomDTO.PlaceVoteRoomCreateResponse response = placeVoteRoomService.createPlaceVoteRoom(request);
        return ResponseEntity.ok(DataResponse.from(response));
    }

}
