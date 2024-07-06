package middle_point_search.backend.domains.PlaceVoteRoom.service;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.AlreadyVotedException;
import middle_point_search.backend.common.exception.DuplicateVoteRoomException;
import middle_point_search.backend.common.security.dto.CustomUserDetails;
import middle_point_search.backend.domains.PlaceVoteRoom.domain.PlaceVoteCandidate;
import middle_point_search.backend.domains.PlaceVoteRoom.domain.PlaceVoteRoom;
import middle_point_search.backend.domains.PlaceVoteRoom.dto.PlaceVoteCandidateDTO;
import middle_point_search.backend.domains.PlaceVoteRoom.dto.PlaceVoteRequestDTO;
import middle_point_search.backend.domains.PlaceVoteRoom.dto.PlaceVoteRoomDTO;
import middle_point_search.backend.domains.PlaceVoteRoom.dto.PlaceVoteRoomRequestDTO;
import middle_point_search.backend.domains.PlaceVoteRoom.repository.PlaceVoteRoomRepository;
import middle_point_search.backend.domains.member.repository.MemberRepository;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.repository.RoomRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import middle_point_search.backend.domains.member.domain.Member;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceVoteRoomService {

    private final PlaceVoteRoomRepository placeVoteRoomRepository;
    private final RoomRepository roomRepository;
    private final MemberRepository memberRepository;

    // 장소투표방 생성
    public PlaceVoteRoomDTO.PlaceVoteRoomCreateResponse createPlaceVoteRoom(PlaceVoteRoomRequestDTO request) {
        Room room = roomRepository.findByIdentityNumber(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid room identity number"));

        PlaceVoteRoom placeVoteRoom = new PlaceVoteRoom(room, request.isDuplication(), request.getNumVoter());

        List<PlaceVoteCandidate> placeVoteCandidates = request.getPlaceCandidates().stream()
                .map(candidateName -> new PlaceVoteCandidate(candidateName, placeVoteRoom))
                .collect(Collectors.toList());
        placeVoteRoom.setPlaceVoteCandidates(placeVoteCandidates);

        try {
            // 먼저 placeVoteRoom을 저장하여 ID를 생성
            PlaceVoteRoom savedPlaceVoteRoom = placeVoteRoomRepository.saveAndFlush(placeVoteRoom);
            // URL 생성 및 설정
            String votePageUrl = generateVotePageUrl(savedPlaceVoteRoom);
            savedPlaceVoteRoom.setUrl(votePageUrl);
            placeVoteRoomRepository.save(savedPlaceVoteRoom); // URL 설정된 객체를 다시 저장

            List<PlaceVoteCandidateDTO> candidates = savedPlaceVoteRoom.getPlaceVoteCandidates()
                    .stream()
                    .map(candidate -> PlaceVoteCandidateDTO.from(candidate.getId(), candidate.getName()))
                    .collect(Collectors.toList());

            return PlaceVoteRoomDTO.PlaceVoteRoomCreateResponse.from(savedPlaceVoteRoom.getId(), savedPlaceVoteRoom.getUrl());
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateVoteRoomException();
        }
    }


    private String generateVotePageUrl(PlaceVoteRoom placeVoteRoom) {
        String baseUrl = "http://api/place-vote-room/";
        return baseUrl + placeVoteRoom.getId();
    }

    // 장소투표방 조회
    public PlaceVoteRoomDTO.PlaceVoteInfoResponse getPlaceVoteRoom(Long placeVoteRoomId) {
        PlaceVoteRoom placeVoteRoom = placeVoteRoomRepository.findById(placeVoteRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid place vote room ID: " + placeVoteRoomId));

        List<PlaceVoteRoomDTO.PlaceVoteInfoResponse.PlaceVoteCandidateInfo> candidates = placeVoteRoom.getPlaceVoteCandidates().stream()
                .map(candidate -> new PlaceVoteRoomDTO.PlaceVoteInfoResponse.PlaceVoteCandidateInfo(
                        candidate.getId(),
                        candidate.getName(),
                        candidate.getVoters().size(),
                        candidate.getVoters().stream().map(v -> v.getMember().getName()).collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        return new PlaceVoteRoomDTO.PlaceVoteInfoResponse(candidates, placeVoteRoom.getNumVoter(), placeVoteRoom.isDuplication());
    }


    // 투표 처리
    public void vote(Long placeVoteRoomId, PlaceVoteRequestDTO voteRequest) {
        PlaceVoteRoom placeVoteRoom = placeVoteRoomRepository.findById(placeVoteRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid place vote room ID: " + placeVoteRoomId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Authentication is required");
        }

        // UserDetails를 통해 사용자 정보 가져오기
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails)) {
            throw new IllegalArgumentException("Principal is not of type CustomUserDetails");
        }

        CustomUserDetails userDetails = (CustomUserDetails) principal;
        Long memberId = userDetails.getId(); // memberId 가져오기
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + memberId));

        // 투표할 후보지에 대해 이미 투표했는지 확인
        for (Long candidateId : voteRequest.getChoicePlaces()) {
            PlaceVoteCandidate candidate = placeVoteRoom.getPlaceVoteCandidates().stream()
                    .filter(c -> c.getId().equals(candidateId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid candidate ID: " + candidateId));
            if (candidate.hasVoter(member)) {
                throw new AlreadyVotedException();
            }
        }

        // 중복 투표가 가능할 경우 여러 후보지에 투표
        if (placeVoteRoom.isDuplication()) {
            voteRequest.getChoicePlaces().forEach(candidateId -> {
                PlaceVoteCandidate candidate = placeVoteRoom.getPlaceVoteCandidates().stream()
                        .filter(c -> c.getId().equals(candidateId))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Invalid candidate ID: " + candidateId));
                candidate.addVoter(member);
            });
        } else {
            // 중복 투표가 불가능할 경우, 모든 투표 후보에서 현재 사용자를 제거한 후 새로운 투표 추가
            placeVoteRoom.getPlaceVoteCandidates().forEach(candidate -> candidate.removeVoter(member));
            voteRequest.getChoicePlaces().forEach(candidateId -> {
                PlaceVoteCandidate candidate = placeVoteRoom.getPlaceVoteCandidates().stream()
                        .filter(c -> c.getId().equals(candidateId))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Invalid candidate ID: " + candidateId));
                candidate.addVoter(member);
            });

        }
        placeVoteRoomRepository.save(placeVoteRoom);
    }

    // 투표 수정 처리
    public void updateVote(Long placeVoteRoomId, PlaceVoteRequestDTO voteRequest) {
        PlaceVoteRoom placeVoteRoom = placeVoteRoomRepository.findById(placeVoteRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid place vote room ID: " + placeVoteRoomId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Authentication is required");
        }

        // UserDetails를 통해 사용자 정보 가져오기
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails)) {
            throw new IllegalArgumentException("Principal is not of type CustomUserDetails");
        }

        CustomUserDetails userDetails = (CustomUserDetails) principal;
        Long memberId = userDetails.getId(); // memberId 가져오기
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + memberId));

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
                    .orElseThrow(() -> new IllegalArgumentException("Invalid candidate ID: " + candidateId));
            candidate.addUpdateVoter(member);
        });

        placeVoteRoomRepository.save(placeVoteRoom);
    }
}