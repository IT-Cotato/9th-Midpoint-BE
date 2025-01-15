package middle_point_search.backend.domains.placeVoteRoom.service;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.memberRoom.service.MemberRoomValidateService;
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteCandidate;
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteCandidateMember;
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteRoom;
import middle_point_search.backend.domains.placeVoteRoom.dto.PlaceVoteDTO.PlaceVoteRequest;
import middle_point_search.backend.domains.placeVoteRoom.dto.PlaceVoteDTO.PlaceVoteResultsFindResponse;
import middle_point_search.backend.domains.placeVoteRoom.dto.PlaceVoteRoomDTO.VotedAndVoteItemResponse;
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
	private final MemberRoomValidateService memberRoomValidateService;

	// 투표 처리
	@Transactional(rollbackFor = {CustomException.class})
	public void vote(Member member, String roomId, PlaceVoteRequest voteRequest) {
		// 방에 대한 회원인지 확인
		memberRoomValidateService.validateAuthorizedMember(member.getId(), roomId);

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
	public void updateVote(Member member, String roomId, PlaceVoteRequest voteRequest) {
		// 방에 대한 회원인지 확인
		memberRoomValidateService.validateAuthorizedMember(member.getId(), roomId);

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

	// 내 투표 조회
	public VotedAndVoteItemResponse findVotedAndVoteItem(Member member, String roomId) {
		// 방에 대한 회원인지 확인
		memberRoomValidateService.validateAuthorizedMember(member.getId(), roomId);

		return placeVoteCandidateMemberRepository.findByPlaceVoteCandidate_PlaceVoteRoom_Room_IdAndMember(
				roomId,
				member)
			.map(placeVoteCandidateMember -> {
				Long id = placeVoteCandidateMember.getPlaceVoteCandidate().getId();

				return VotedAndVoteItemResponse.from(true, id);
			})
			.orElseGet(() -> VotedAndVoteItemResponse.from(false, null));
	}

	// 장소투표 결과 조회
	public List<PlaceVoteResultsFindResponse> findPlaceVoteResults(Long memberId, String roomId) {
		// 방에 대한 회원인지 확인
		memberRoomValidateService.validateAuthorizedMember(memberId, roomId);

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
				placeVoteCandidate.getVoters().stream().map(v -> v.getMember().getEmail()).collect(Collectors.toList())))
			.collect(Collectors.toList());
	}
}
