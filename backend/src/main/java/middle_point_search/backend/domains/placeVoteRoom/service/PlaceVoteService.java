package middle_point_search.backend.domains.placeVoteRoom.service;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.repository.MemberRepository;
import middle_point_search.backend.domains.memberRoom.service.MemberRoomValidateService;
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVote;
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteCandidate;
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteRoom;
import middle_point_search.backend.domains.placeVoteRoom.dto.request.UpdatePlaceVoteRequest;
import middle_point_search.backend.domains.placeVoteRoom.dto.request.VotePlaceRequest;
import middle_point_search.backend.domains.placeVoteRoom.dto.response.FindPlaceVoteResultsResponse;
import middle_point_search.backend.domains.placeVoteRoom.dto.response.FindVotedAndVoteItemResponse;
import middle_point_search.backend.domains.placeVoteRoom.repository.PlaceVoteCandidateRepository;
import middle_point_search.backend.domains.placeVoteRoom.repository.PlaceVoteRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceVoteService {

	private final PlaceVoteRoomService placeVoteRoomService;
	private final PlaceVoteCandidateRepository placeVoteCandidateRepository;
	private final PlaceVoteRepository placeVoteRepository;
	private final MemberRoomValidateService memberRoomValidateService;
	private final MemberRepository memberRepository;

	// 투표 처리
	@Transactional(rollbackFor = {CustomException.class})
	public void votePlace(Long memberId, String roomId, VotePlaceRequest voteRequest) {
		// 방에 대한 회원인지 확인
		memberRoomValidateService.validateAuthorizedMember(memberId, roomId);

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> CustomException.from(MEMBER_NOT_FOUND));
		PlaceVoteRoom placeVoteRoom = placeVoteRoomService.findByRoomId(roomId)
			.orElseThrow(() -> CustomException.from(VOTE_ROOM_NOT_FOUND));

		// 투표 했는지 확인
		validateAlreadyVoted(placeVoteRoom, member);

		// 투표 후보 조회
		long placeVoteId = voteRequest.choicePlace();
		PlaceVoteCandidate candidate = placeVoteCandidateRepository.findById(placeVoteId)
			.orElseThrow(() -> CustomException.from(CANDIDATE_NOT_FOUND));

		placeVoteRepository.save(PlaceVote.builder()
			.placeVoteCandidate(candidate)
			.member(member)
			.placeVoteRoom(placeVoteRoom)
			.build());
	}

	// 재투표
	@Transactional(rollbackFor = {CustomException.class})
	public void updateVote(Long memberId, String roomId, UpdatePlaceVoteRequest request) {
		// 방에 대한 회원인지 확인
		memberRoomValidateService.validateAuthorizedMember(memberId, roomId);

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> CustomException.from(MEMBER_NOT_FOUND));
		PlaceVoteRoom placeVoteRoom = placeVoteRoomService.findByRoomId(roomId)
			.orElseThrow(() -> CustomException.from(VOTE_ROOM_NOT_FOUND));

		validateAlreadyVoted(placeVoteRoom, member);

		// 기존 투표 삭제
		placeVoteRepository.deleteByPlaceVoteCandidate_PlaceVoteRoomAndMember(placeVoteRoom, member);

		// 새로 받은 항목으로 업데이트
		long placeVoteCandidateId = request.choicePlace();
		PlaceVoteCandidate candidate = placeVoteCandidateRepository.findById(placeVoteCandidateId)
			.orElseThrow(() -> CustomException.from(CANDIDATE_NOT_FOUND));

		placeVoteRepository.save(PlaceVote.builder()
			.placeVoteCandidate(candidate)
			.member(member)
			.placeVoteRoom(placeVoteRoom)
			.build());
	}

	// 투표 했는지 확인
	private void validateAlreadyVoted(PlaceVoteRoom placeVoteRoom, Member member) {
		if (placeVoteRepository.existsByPlaceVoteCandidate_PlaceVoteRoomAndMember(
			placeVoteRoom, member)) {
			throw CustomException.from(ALREADY_VOTED);
		}
	}

	// 내 투표 조회
	public FindVotedAndVoteItemResponse findVotedAndVoteItem(Long memberId, String roomId) {
		// 방에 대한 회원인지 확인
		memberRoomValidateService.validateAuthorizedMember(memberId, roomId);

		return placeVoteRepository.findByPlaceVoteCandidate_PlaceVoteRoom_Room_IdAndMember_Id(
				roomId,
				memberId)
			.map(placeVote -> {
				Long id = placeVote.getPlaceVoteCandidate().getId();

				return FindVotedAndVoteItemResponse.from(true, id);
			})
			.orElseGet(() -> FindVotedAndVoteItemResponse.from(false, null));
	}

	// 장소투표 결과 조회
	public List<FindPlaceVoteResultsResponse> findPlaceVoteResults(Long memberId, String roomId) {
		// 방에 대한 회원인지 확인
		memberRoomValidateService.validateAuthorizedMember(memberId, roomId);

		// 장소 투표방 조회
		PlaceVoteRoom placeVoteRoom = placeVoteRoomService.findByRoomId(roomId)
			.orElseThrow(() -> CustomException.from(VOTE_ROOM_NOT_FOUND));

		// 결과 조회
		return placeVoteCandidateRepository.findAllByPlaceVoteRoom(placeVoteRoom)
			.stream()
			.map(placeVoteCandidate -> {
				List<PlaceVote> votes = placeVoteRepository.findAllByPlaceVoteCandidate(placeVoteCandidate);
				return FindPlaceVoteResultsResponse.of(placeVoteCandidate, votes);
			})
			.collect(Collectors.toList());
	}
}
