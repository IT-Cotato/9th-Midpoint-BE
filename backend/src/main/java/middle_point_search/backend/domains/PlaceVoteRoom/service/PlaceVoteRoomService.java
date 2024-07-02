package middle_point_search.backend.domains.PlaceVoteRoom.service;


import middle_point_search.backend.domains.PlaceVoteRoom.domain.PlaceVoteCandidate;
import middle_point_search.backend.domains.PlaceVoteRoom.domain.PlaceVoteRoom;
import middle_point_search.backend.domains.PlaceVoteRoom.dto.PlaceVoteCandidateDTO;
import middle_point_search.backend.domains.PlaceVoteRoom.dto.PlaceVoteRoomDTO;
import middle_point_search.backend.domains.PlaceVoteRoom.dto.PlaceVoteRoomRequestDTO;
import middle_point_search.backend.domains.PlaceVoteRoom.repository.PlaceVoteRoomRepository;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaceVoteRoomService {

    @Autowired
    private  PlaceVoteRoomRepository placeVoteRoomRepository;
    @Autowired
    private  RoomRepository roomRepository;


    public PlaceVoteRoomDTO.PlaceVoteRoomCreateResponse createPlaceVoteRoom(PlaceVoteRoomRequestDTO request) {
        Room room = roomRepository.findByIdentityNumber(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid room identity number"));

        PlaceVoteRoom placeVoteRoom = new PlaceVoteRoom(room, request.isDuplication(), request.getNumVoter());

        List<PlaceVoteCandidate> placeVoteCandidates = request.getPlaceCandidates().stream()
                .map(candidateName -> new PlaceVoteCandidate(candidateName, placeVoteRoom))
                .collect(Collectors.toList());
        placeVoteRoom.setPlaceVoteCandidates(placeVoteCandidates);

        PlaceVoteRoom savedPlaceVoteRoom = placeVoteRoomRepository.save(placeVoteRoom);

        // URL 생성 및 설정
        String votePageUrl = generateVotePageUrl(savedPlaceVoteRoom);
        savedPlaceVoteRoom.setUrl(votePageUrl);
        placeVoteRoomRepository.save(savedPlaceVoteRoom); // URL 설정된 객체를 다시 저장

        List<PlaceVoteCandidateDTO> candidates = savedPlaceVoteRoom.getPlaceVoteCandidates()
                .stream()
                .map(candidate -> PlaceVoteCandidateDTO.from(candidate.getId(), candidate.getName()))
                .collect(Collectors.toList());

        return PlaceVoteRoomDTO.PlaceVoteRoomCreateResponse.from(savedPlaceVoteRoom.getId(), savedPlaceVoteRoom.getUrl());
    }

    private String generateVotePageUrl(PlaceVoteRoom placeVoteRoom) {
        String baseUrl = "http://api/place-vote-room/";
        return baseUrl + placeVoteRoom.getId();
    }

}
