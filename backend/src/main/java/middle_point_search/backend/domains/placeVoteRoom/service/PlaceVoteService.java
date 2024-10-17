package middle_point_search.backend.domains.placeVoteRoom.service;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteCandidate;
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteCandidateMember;
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteRoom;
import middle_point_search.backend.domains.placeVoteRoom.dto.PlaceVoteDTO.PlaceVoteCandidatesFindResponse.PlaceCandidate;
import middle_point_search.backend.domains.placeVoteRoom.dto.PlaceVoteDTO.PlaceVoteRequest;
import middle_point_search.backend.domains.placeVoteRoom.dto.PlaceVoteDTO.PlaceVoteResultsFindResponse;
import middle_point_search.backend.domains.placeVoteRoom.dto.PlaceVoteDTO.PlaceVoteCandidatesFindResponse;
import middle_point_search.backend.domains.placeVoteRoom.dto.PlaceVoteRoomDTO;
import middle_point_search.backend.domains.placeVoteRoom.repository.PlaceVoteCandidateMemberRepository;
import middle_point_search.backend.domains.placeVoteRoom.repository.PlaceVoteCandidateRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceVoteService {

	private final PlaceVoteMemberService placeVoteMemberService;
	private final PlaceVoteRoomService placeVoteRoomService;
	private final PlaceVoteCandidateRepository placeVoteCandidateRepository;
	private final PlaceVoteCandidateMemberRepository placeVoteCandidateMemberRepository;

	// 투표 처리
	@Transactional(rollbackFor = {CustomException.class})
	public void vote(Member member, Long roomId, PlaceVoteRequest voteRequest) {
		PlaceVoteRoom placeVoteRoom = placeVoteRoomService.findByRoomId(roomId)
			.orElseThrow(() -> CustomException.from(VOTE_ROOM_NOT_FOUND));

		// 투표 했는지 확인
		boolean alreadyVoted = placeVoteMemberService.existsByPlaceVote_PlaceVoteRoomAndMember(
			placeVoteRoom,
			member);
		if (alreadyVoted) {
			throw CustomException.from(ALREADY_VOTED);
		}

		// 투표 후보 조회
		long placeVoteId = voteRequest.getChoicePlace();
		PlaceVoteCandidate candidate = placeVoteCandidateRepository.findById(placeVoteId)
			.orElseThrow(() -> CustomException.from(CANDIDATE_NOT_FOUND));

		PlaceVoteCandidateMember placeVoteCandidateMember = new PlaceVoteCandidateMember(candidate, member);
		placeVoteCandidateMemberRepository.save(placeVoteCandidateMember);
	}

	// 재투표
	@Transactional(rollbackFor = {CustomException.class})
	public void updateVote(Member member, Long roomId, PlaceVoteRequest voteRequest) {
		PlaceVoteRoom placeVoteRoom = placeVoteRoomService.findByRoomId(roomId)
			.orElseThrow(() -> CustomException.from(VOTE_ROOM_NOT_FOUND));

		// 투표 했는지 확인
		boolean alreadyVoted = placeVoteCandidateMemberRepository.existsByPlaceVoteCandidate_PlaceVoteRoomAndMember(
			placeVoteRoom, member);
		if (!alreadyVoted) {
			throw CustomException.from(VOTE_NOT_FOUND);
		}

		// 기존 투표 삭제
		placeVoteCandidateMemberRepository.deleteByPlaceVoteCandidate_PlaceVoteRoomAndMember(placeVoteRoom, member);

		// 새로 받은 항목으로 업데이트
		long placeVoteCandidateId = voteRequest.getChoicePlace();
		PlaceVoteCandidate candidate = placeVoteCandidateRepository.findById(placeVoteCandidateId)
			.orElseThrow(() -> CustomException.from(CANDIDATE_NOT_FOUND));

		PlaceVoteCandidateMember placeVoteCandidateMember = new PlaceVoteCandidateMember(candidate, member);
		placeVoteCandidateMemberRepository.save(placeVoteCandidateMember);
	}

	// 장소투표방 존재 여부 확인, 존재시 true, 존재하지 않을시 false 반환
	public PlaceVoteCandidatesFindResponse findPlaceVoteCandidates(Long roomId) {

		Optional<PlaceVoteRoom> placeVoteRoomOptional = placeVoteRoomService.findByRoomId(roomId);

		return placeVoteRoomOptional
			.map(placeVoteRoom -> {
				List<PlaceCandidate> placeCandidates = placeVoteRoom.getPlaceVoteCandidates()
					.stream()
					.map(candidate -> new PlaceCandidate(candidate.getId(), candidate.getName(), candidate.getSiDo(),
						candidate.getSiGunGu(), candidate.getRoadNameAddress(), candidate.getAddressLatitude(),
						candidate.getAddressLongitude()))
					.collect(Collectors.toList());

				return PlaceVoteCandidatesFindResponse.from(true, placeCandidates);
			})
			.orElseGet(() -> PlaceVoteCandidatesFindResponse.from(false, null));
	}

	// 내 투표 조회
	public PlaceVoteRoomDTO.VotedAndVoteItemResponse findVotedAndVoteItem(Member member, Long roomId) {
		return placeVoteCandidateMemberRepository.findByPlaceVoteCandidate_PlaceVoteRoom_Room_IdAndMember(
				roomId,
				member)
			.map(placeVoteCandidateMember -> {
				Long id = placeVoteCandidateMember.getPlaceVoteCandidate().getId();

				return PlaceVoteRoomDTO.VotedAndVoteItemResponse.from(true, id);
			})
			.orElseGet(() -> PlaceVoteRoomDTO.VotedAndVoteItemResponse.from(false, null));
	}

	// 장소투표 결과 조회
	public List<PlaceVoteResultsFindResponse> findPlaceVoteResults(Long roomId) {
		// 장소 투표방 조회
		PlaceVoteRoom placeVoteRoom = placeVoteRoomService.findByRoomId(roomId)
			.orElseThrow(() -> CustomException.from(VOTE_ROOM_NOT_FOUND));

		// 결과 조회
		return placeVoteRoom.getPlaceVoteCandidates().stream()
			.map(placeVoteCandidate -> new PlaceVoteResultsFindResponse(
				placeVoteCandidate.getId(),
				placeVoteCandidate.getName(),
				placeVoteCandidate.getSiDo(),
				placeVoteCandidate.getSiGunGu(),
				placeVoteCandidate.getRoadNameAddress(),
				placeVoteCandidate.getAddressLatitude(),
				placeVoteCandidate.getAddressLatitude(),
				placeVoteCandidate.getCount(),
				placeVoteCandidate.getVoters().stream().map(v -> v.getMember().getName()).collect(Collectors.toList())))
			.collect(Collectors.toList());
	}
}
