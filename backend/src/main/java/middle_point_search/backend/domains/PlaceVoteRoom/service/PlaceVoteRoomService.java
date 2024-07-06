package middle_point_search.backend.domains.PlaceVoteRoom.service;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.AlreadyVotedException;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.common.exception.DuplicateVoteRoomException;
import middle_point_search.backend.common.exception.errorCode.CommonErrorCode;
import middle_point_search.backend.common.util.MemberLoader;
import middle_point_search.backend.domains.PlaceVoteRoom.domain.PlaceVoteCandidate;
import middle_point_search.backend.domains.PlaceVoteRoom.domain.PlaceVoteRoom;
import middle_point_search.backend.domains.PlaceVoteRoom.dto.PlaceVoteCandidateDTO;
import middle_point_search.backend.domains.PlaceVoteRoom.dto.PlaceVoteRequestDTO;
import middle_point_search.backend.domains.PlaceVoteRoom.dto.PlaceVoteRoomRequestDTO;
import middle_point_search.backend.domains.PlaceVoteRoom.repository.PlaceVoteRoomRepository;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static middle_point_search.backend.domains.PlaceVoteRoom.dto.PlaceVoteRoomDTO.PlaceVoteInfoResponse;
import static middle_point_search.backend.domains.PlaceVoteRoom.dto.PlaceVoteRoomDTO.PlaceVoteRoomCreateResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceVoteRoomService {

    private final PlaceVoteRoomRepository placeVoteRoomRepository;
    private final RoomRepository roomRepository;
    private final MemberLoader memberLoader;

    // 장소투표방 생성
    @Transactional
    public PlaceVoteRoomCreateResponse createPlaceVoteRoom(PlaceVoteRoomRequestDTO request) {
        Room room = roomRepository.findByIdentityNumber(request.getRoomId())
                .orElseThrow(() -> new CustomException(CommonErrorCode.INVALID_PARAMETER));

        boolean exists = placeVoteRoomRepository.existsByRoomAndDuplication(room, request.isDuplication());
        if (exists) {
            throw new DuplicateVoteRoomException();
        }

        PlaceVoteRoom placeVoteRoom = new PlaceVoteRoom(room, request.isDuplication(), request.getNumVoter());

        List<PlaceVoteCandidate> placeVoteCandidates = request.getPlaceCandidates().stream()
                .map(candidateName -> new PlaceVoteCandidate(candidateName, placeVoteRoom))
                .collect(Collectors.toList());
        placeVoteRoom.setPlaceVoteCandidates(placeVoteCandidates);

        // placeVoteRoom을 저장하여 ID를 생성
        PlaceVoteRoom savedPlaceVoteRoom = placeVoteRoomRepository.saveAndFlush(placeVoteRoom);

        // URL 생성 및 설정
        String votePageUrl = generateVotePageUrl(savedPlaceVoteRoom);
        savedPlaceVoteRoom.setUrl(votePageUrl);
        placeVoteRoomRepository.save(savedPlaceVoteRoom); // URL 설정된 객체를 다시 저장

        List<PlaceVoteCandidateDTO> candidates = savedPlaceVoteRoom.getPlaceVoteCandidates()
                .stream()
                .map(candidate -> PlaceVoteCandidateDTO.from(candidate.getId(), candidate.getName()))
                .collect(Collectors.toList());

        return PlaceVoteRoomCreateResponse.from(savedPlaceVoteRoom.getId(), savedPlaceVoteRoom.getUrl());
    }


    private String generateVotePageUrl(PlaceVoteRoom placeVoteRoom) {
        String baseUrl = "http://api/place-vote-room/";
        return baseUrl + placeVoteRoom.getId();
    }

    // 장소투표방 조회
    public PlaceVoteInfoResponse getPlaceVoteRoom(Long placeVoteRoomId) {
        PlaceVoteRoom placeVoteRoom = placeVoteRoomRepository.findById(placeVoteRoomId)
                .orElseThrow(() -> new CustomException(CommonErrorCode.INVALID_PARAMETER));

        List<PlaceVoteInfoResponse.PlaceVoteCandidateInfo> candidates = placeVoteRoom.getPlaceVoteCandidates().stream()
                .map(candidate -> new PlaceVoteInfoResponse.PlaceVoteCandidateInfo(
                        candidate.getId(),
                        candidate.getName(),
                        candidate.getCount(),
                        candidate.getVoters().stream().map(v -> v.getMember().getName()).collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        return new PlaceVoteInfoResponse(candidates, placeVoteRoom.getNumVoter(), placeVoteRoom.isDuplication());
    }

    // 투표 처리
    @Transactional
    public void vote(Long placeVoteRoomId, PlaceVoteRequestDTO voteRequest) {
        PlaceVoteRoom placeVoteRoom = placeVoteRoomRepository.findById(placeVoteRoomId)
                .orElseThrow(() -> new CustomException(CommonErrorCode.INVALID_PARAMETER));
        Member member = memberLoader.getMember();
        // 투표할 후보지에 대해 이미 투표했는지 확인
        for (Long candidateId : voteRequest.getChoicePlaces()) {
            PlaceVoteCandidate candidate = placeVoteRoom.getPlaceVoteCandidates().stream()
                    .filter(c -> c.getId().equals(candidateId))
                    .findFirst()
                    .orElseThrow(() -> new CustomException(CommonErrorCode.INVALID_PARAMETER));
            if (candidate.hasVoter(member)) {
                throw new AlreadyVotedException();
            }
        }

        placeVoteRoom.getPlaceVoteCandidates().forEach(candidate -> candidate.removeVoter(member));
        voteRequest.getChoicePlaces().forEach(candidateId -> {
            PlaceVoteCandidate candidate = placeVoteRoom.getPlaceVoteCandidates().stream().
                    filter(c -> c.getId().equals(candidateId))
                    .findFirst()
                    .orElseThrow(() -> new CustomException(CommonErrorCode.INVALID_PARAMETER));
            candidate.addVoter(member);
        });
        placeVoteRoomRepository.save(placeVoteRoom);
    }

    // 투표 수정 처리
    @Transactional
    public void updateVote(Long placeVoteRoomId, PlaceVoteRequestDTO voteRequest) {
        PlaceVoteRoom placeVoteRoom = placeVoteRoomRepository.findById(placeVoteRoomId)
                .orElseThrow(() -> new CustomException(CommonErrorCode.INVALID_PARAMETER));

        Member member = memberLoader.getMember();
        // 기존 투표 제거
        placeVoteRoom.getPlaceVoteCandidates().forEach(candidate -> {
            if (candidate.hasVoter(member)) {
                candidate.removeVoter(member);
            }
        });
        // 새로 받은 항목으로 업데이트
        voteRequest.getChoicePlaces().forEach(candidateId -> {
            PlaceVoteCandidate candidate = placeVoteRoom.getPlaceVoteCandidates().stream()
                    .filter(c -> c.getId().equals(candidateId))
                    .findFirst()
                    .orElseThrow(() -> new CustomException(CommonErrorCode.INVALID_PARAMETER));
            candidate.addUpdateVoter(member);
        });
        placeVoteRoomRepository.save(placeVoteRoom);
    }
}