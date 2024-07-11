package middle_point_search.backend.domains.PlaceVoteRoom.service;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.AlreadyVotedException;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.common.exception.DuplicateVoteRoomException;
import middle_point_search.backend.common.exception.errorCode.CommonErrorCode;
import middle_point_search.backend.common.exception.errorCode.UserErrorCode;
import middle_point_search.backend.common.util.MemberLoader;
import middle_point_search.backend.domains.PlaceVoteRoom.domain.PlaceVoteCandidate;
import middle_point_search.backend.domains.PlaceVoteRoom.domain.PlaceVoteCandidateMember;
import middle_point_search.backend.domains.PlaceVoteRoom.domain.PlaceVoteRoom;
import middle_point_search.backend.domains.PlaceVoteRoom.repository.PlaceVoteCandidateMemberRepository;
import middle_point_search.backend.domains.PlaceVoteRoom.repository.PlaceVoteRoomRepository;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static middle_point_search.backend.domains.PlaceVoteRoom.dto.PlaceVoteRoomDTO.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceVoteRoomService {

    private final PlaceVoteRoomRepository placeVoteRoomRepository;
    private final PlaceVoteCandidateMemberRepository placeVoteCandidateMemberRepository;
    private final RoomRepository roomRepository;
    private final MemberLoader memberLoader;

    // 장소투표방 생성
    @Transactional
    public PlaceVoteRoomCreateResponse createPlaceVoteRoom(PlaceVoteRoomCreateRequest request) {
        Member member = memberLoader.getMember();
        String roomId = member.getRoom().getIdentityNumber();
        Room room = roomRepository.findByIdentityNumber(roomId).orElseThrow(() -> new CustomException(UserErrorCode.ROOM_NOT_FOUND));

        boolean exists = placeVoteRoomRepository.existsByRoom(room);
        if (exists) {
            throw new DuplicateVoteRoomException();
        }
        PlaceVoteRoom placeVoteRoom = new PlaceVoteRoom(room);

        List<PlaceVoteCandidate> placeVoteCandidates = request.getPlaceCandidates().stream().map(candidateName -> new PlaceVoteCandidate(candidateName, placeVoteRoom)).collect(Collectors.toList());
        placeVoteRoom.setPlaceVoteCandidates(placeVoteCandidates);

        PlaceVoteRoom savedPlaceVoteRoom = placeVoteRoomRepository.save(placeVoteRoom);

        return PlaceVoteRoomCreateResponse.from(savedPlaceVoteRoom.getId());
    }

    // 장소투표방 조회
    public PlaceVoteInfoResponse getPlaceVoteRoom() {
        Member member = memberLoader.getMember();
        String roomId = member.getRoom().getIdentityNumber();

        PlaceVoteRoom placeVoteRoom = placeVoteRoomRepository.findByRoom_IdentityNumber(roomId).orElseThrow(() -> new CustomException(UserErrorCode.VOTE_ROOM_NOT_FOUND));
        List<PlaceVoteInfoResponse.PlaceVoteCandidateInfo> candidates = placeVoteRoom.getPlaceVoteCandidates().stream().map(candidate -> new PlaceVoteInfoResponse.PlaceVoteCandidateInfo(candidate.getId(), candidate.getName(), candidate.getCount(), candidate.getVoters().stream().map(v -> v.getMember().getName()).collect(Collectors.toList()))).collect(Collectors.toList());

        return new PlaceVoteInfoResponse(candidates);
    }

    // 투표 처리
    @Transactional
    public void vote(PlaceVoteRequest voteRequest) {
        Member member = memberLoader.getMember();
        String roomId = member.getRoom().getIdentityNumber();
        PlaceVoteRoom placeVoteRoom = placeVoteRoomRepository.findByRoom_IdentityNumber(roomId).orElseThrow(() -> new CustomException(UserErrorCode.VOTE_ROOM_NOT_FOUND));

        boolean alreadyVoted = placeVoteCandidateMemberRepository.existsByPlaceVoteCandidate_PlaceVoteRoomAndMember(placeVoteRoom, member);
        if (alreadyVoted) {
            throw new AlreadyVotedException();
        }
        placeVoteRoom.getPlaceVoteCandidates().forEach(candidate -> candidate.removeVoter(member));
        voteRequest.getChoicePlaces().forEach(candidateId -> {
            PlaceVoteCandidate candidate = placeVoteRoom.getPlaceVoteCandidates().stream().filter(c -> c.getId().equals(candidateId)).findFirst().orElseThrow(() -> new CustomException(UserErrorCode.VOTE_ROOM_NOT_FOUND));
            candidate.addVoter(member);
        });
        placeVoteRoomRepository.save(placeVoteRoom);
    }

    // 재투표
    @Transactional
    public void updateVote(PlaceVoteRequest voteRequest) {

        Member member = memberLoader.getMember();
        String roomId = member.getRoom().getIdentityNumber();
        PlaceVoteRoom placeVoteRoom = placeVoteRoomRepository.findByRoom_IdentityNumber(roomId).orElseThrow(() -> new CustomException(UserErrorCode.VOTE_ROOM_NOT_FOUND));
        //기존투표제거
        boolean alreadyVoted = placeVoteCandidateMemberRepository.existsByPlaceVoteCandidate_PlaceVoteRoomAndMember(placeVoteRoom, member);
        if (!alreadyVoted) {
            throw new CustomException(UserErrorCode.VOTE_NOT_FOUND);
        }

        List<PlaceVoteCandidateMember> existingVotes = placeVoteCandidateMemberRepository.findAllByPlaceVoteCandidate_PlaceVoteRoomAndMember(placeVoteRoom, member);
        placeVoteCandidateMemberRepository.deleteAll(existingVotes);

        // 새로 받은 항목으로 업데이트
        voteRequest.getChoicePlaces().forEach(candidateId -> {
            PlaceVoteCandidate candidate = placeVoteRoom.getPlaceVoteCandidates().stream().filter(c -> c.getId().equals(candidateId)).findFirst().orElseThrow(() -> new CustomException(CommonErrorCode.INVALID_PARAMETER));
            candidate.addUpdateVoter(member);
        });
        placeVoteRoomRepository.save(placeVoteRoom);
    }

    //투표방생성여부
    public boolean hasPlaceVoteRoom() {
        Member member = memberLoader.getMember();
        String roomId = member.getRoom().getIdentityNumber();
        Room room = roomRepository.findByIdentityNumber(roomId).orElseThrow(() -> new CustomException(CommonErrorCode.INVALID_PARAMETER));

        return placeVoteRoomRepository.existsByRoom(room);
    }

    //투표여부
    public boolean hasVoted() {
        Member member = memberLoader.getMember();
        String roomId = member.getRoom().getIdentityNumber();
        PlaceVoteRoom placeVoteRoom = placeVoteRoomRepository.findByRoom_IdentityNumber(roomId).orElseThrow(() -> new CustomException(UserErrorCode.VOTE_ROOM_NOT_FOUND));

        return placeVoteCandidateMemberRepository.existsByPlaceVoteCandidate_PlaceVoteRoomAndMember(placeVoteRoom, member);
    }
}