package middle_point_search.backend.domains.placeVoteRoom.service;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteCandidate;
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteCandidateMember;
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteRoom;
import middle_point_search.backend.domains.placeVoteRoom.dto.PlaceVoteRoomDTO.PlaceVoteRoomGetResponse.PlaceCandidates;
import middle_point_search.backend.domains.placeVoteRoom.repository.PlaceVoteCandidateMemberRepository;
import middle_point_search.backend.domains.placeVoteRoom.repository.PlaceVoteCandidateRepository;
import middle_point_search.backend.domains.placeVoteRoom.repository.PlaceVoteRoomRepository;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.room.domain.Room;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;
import static middle_point_search.backend.domains.placeVoteRoom.dto.PlaceVoteRoomDTO.*;
import static middle_point_search.backend.domains.placeVoteRoom.dto.PlaceVoteRoomDTO.PlaceVoteInfoResponse.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceVoteRoomService {

	private final PlaceVoteRoomRepository placeVoteRoomRepository;
	private final PlaceVoteCandidateMemberRepository placeVoteCandidateMemberRepository;
	private final PlaceVoteCandidateRepository placeVoteCandidateRepository;

	// 장소투표방 생성
	@Transactional(rollbackFor = {CustomException.class})
	public PlaceVoteRoomCreateResponse createPlaceVoteRoom(Room room, PlaceVoteRoomCreateRequest request) {

		boolean exists = placeVoteRoomRepository.existsByRoom(room);

		if (exists) {
			throw new CustomException(DUPLICATE_VOTE_ROOM);
		}

		PlaceVoteRoom placeVoteRoom = new PlaceVoteRoom(room, request.getPlaceCandidates());
		PlaceVoteRoom savedPlaceVoteRoom = placeVoteRoomRepository.save(placeVoteRoom);

		return PlaceVoteRoomCreateResponse.from(savedPlaceVoteRoom.getId());
	}

	//장소투표방 재생성
	@Transactional(rollbackFor = {CustomException.class})
	public PlaceVoteRoomCreateResponse recreatePlaceVoteRoom(Room room, PlaceVoteRoomCreateRequest request) {

		// 기존 투표방 삭제
		PlaceVoteRoom existingPlaceVoteRoom = placeVoteRoomRepository.findByRoom(room)
			.orElseThrow(() -> new CustomException(VOTE_ROOM_NOT_FOUND));

		// 먼저 투표와 관련된 모든 데이터 삭제
		placeVoteRoomRepository.delete(existingPlaceVoteRoom);
		placeVoteRoomRepository.flush();

		//투표방 생성
		PlaceVoteRoom placeVoteRoom = new PlaceVoteRoom(room, request.getPlaceCandidates());
		PlaceVoteRoom savedPlaceVoteRoom = placeVoteRoomRepository.save(placeVoteRoom);

		return PlaceVoteRoomCreateResponse.from(savedPlaceVoteRoom.getId());
	}

	// 장소투표방 결과 조회
	public PlaceVoteInfoResponse getPlaceVoteRoomResult(Room room) {

		PlaceVoteRoom placeVoteRoom = placeVoteRoomRepository.findByRoom(room)
			.orElseThrow(() -> new CustomException(VOTE_ROOM_NOT_FOUND));
		List<PlaceVoteCandidateInfo> candidates = placeVoteRoom.getPlaceVoteCandidates()
			.stream()
			.map(candidate -> new PlaceVoteCandidateInfo(candidate.getId(), candidate.getName(), candidate.getSiDo(),
				candidate.getSiGunGu(), candidate.getRoadNameAddress(), candidate.getAddressLatitude(),
				candidate.getAddressLatitude(), candidate.getCount(),
				candidate.getVoters().stream().map(v -> v.getMember().getName()).collect(Collectors.toList())))
			.collect(Collectors.toList());

		return new PlaceVoteInfoResponse(candidates);
	}

	// 투표 처리
	@Transactional(rollbackFor = {CustomException.class})
	public void vote(Member member, Room room, PlaceVoteRequest voteRequest) {

		PlaceVoteRoom placeVoteRoom = placeVoteRoomRepository.findByRoom(room)
			.orElseThrow(() -> new CustomException(VOTE_ROOM_NOT_FOUND));

		boolean alreadyVoted = placeVoteCandidateMemberRepository.existsByPlaceVoteCandidate_PlaceVoteRoomAndMember(
			placeVoteRoom, member);
		if (alreadyVoted) {
			throw new CustomException(ALREADY_VOTED);
		}
		long candidateId = voteRequest.getChoicePlace();
		PlaceVoteCandidate candidate = placeVoteCandidateRepository.findById(candidateId)
			.orElseThrow(() -> new CustomException(CANDIDATE_NOT_FOUND));

		PlaceVoteCandidateMember placeVoteCandidateMember = new PlaceVoteCandidateMember(candidate, member);
		placeVoteCandidateMemberRepository.save(placeVoteCandidateMember);
	}

	// 재투표
	@Transactional(rollbackFor = {CustomException.class})
	public void updateVote(Member member, Room room, PlaceVoteRequest voteRequest) {

		PlaceVoteRoom placeVoteRoom = placeVoteRoomRepository.findByRoom(room)
			.orElseThrow(() -> new CustomException(VOTE_ROOM_NOT_FOUND));

		//기존투표제거
		boolean alreadyVoted = placeVoteCandidateMemberRepository.existsByPlaceVoteCandidate_PlaceVoteRoomAndMember(
			placeVoteRoom, member);
		if (!alreadyVoted) {
			throw new CustomException(VOTE_NOT_FOUND);
		}

		List<PlaceVoteCandidateMember> existingVotes = placeVoteCandidateMemberRepository.findAllByPlaceVoteCandidate_PlaceVoteRoomAndMember(
			placeVoteRoom, member);
		placeVoteCandidateMemberRepository.deleteAll(existingVotes);

		// 새로 받은 항목으로 업데이트
		long candidateId = voteRequest.getChoicePlace();
		PlaceVoteCandidate candidate = placeVoteCandidateRepository.findById(candidateId)
			.orElseThrow(() -> new CustomException(CANDIDATE_NOT_FOUND));

		PlaceVoteCandidateMember placeVoteCandidateMember = new PlaceVoteCandidateMember(candidate, member);
		placeVoteCandidateMemberRepository.save(placeVoteCandidateMember);
	}

	//장소투표방 존재 여부 확인, 존재시 true, 존재하지 않을시 false 반환
	public PlaceVoteRoomGetResponse getPlaceVoteRoomResult(String roomId) {

		Optional<PlaceVoteRoom> placeVoteRoomOptional = placeVoteRoomRepository.findByRoom_IdentityNumber(roomId);

		return placeVoteRoomOptional
			.map(placeVoteRoom -> {
				List<PlaceCandidates> placeCandidates = placeVoteRoom.getPlaceVoteCandidates()
					.stream()
					.map(candidate -> new PlaceCandidates(candidate.getId(), candidate.getName(), candidate.getSiDo(),
						candidate.getSiGunGu(), candidate.getRoadNameAddress(), candidate.getAddressLatitude(),
						candidate.getAddressLongitude()))
					.collect(Collectors.toList());

				return PlaceVoteRoomGetResponse.from(true, placeCandidates);
			})
			.orElseGet(() -> PlaceVoteRoomGetResponse.from(false, null));
	}

	// 사용자가 투표를 했는지 확인하고, 투표한 경우 상세 투표 정보를 반환
	public PlaceVoteStatusResponse getPlaceVoteRoomResultWithVotingInfo(Member member, Room room) {
		Optional<PlaceVoteRoom> placeVoteRoomOptional = placeVoteRoomRepository.findByRoom(room);

		return placeVoteRoomOptional
			.map(placeVoteRoom -> {
				// 사용자가 투표했는지 여부 확인
				boolean hasVoted = placeVoteCandidateMemberRepository.existsByPlaceVoteCandidate_PlaceVoteRoomAndMember(placeVoteRoom, member);

				if (hasVoted) {
					// 사용자가 투표한 장소 목록 생성
					List<PlaceVoteStatusResponse.PlaceCandidates> userVotedCandidates = placeVoteRoom.getPlaceVoteCandidates().stream()
						.filter(candidate -> placeVoteCandidateMemberRepository.existsByPlaceVoteCandidateAndMember(candidate, member))
						.map(candidate -> new PlaceVoteStatusResponse.PlaceCandidates(
							candidate.getId(),
							candidate.getName(),
							candidate.getSiDo(),
							candidate.getSiGunGu(),
							candidate.getRoadNameAddress(),
							candidate.getAddressLatitude(),
							candidate.getAddressLongitude()
						))
						.collect(Collectors.toList());

					// 전체 장소 후보 목록 생성
					List<PlaceVoteStatusResponse.PlaceCandidates> allPlaceCandidates = placeVoteRoom.getPlaceVoteCandidates().stream()
						.map(candidate -> new PlaceVoteStatusResponse.PlaceCandidates(
							candidate.getId(),
							candidate.getName(),
							candidate.getSiDo(),
							candidate.getSiGunGu(),
							candidate.getRoadNameAddress(),
							candidate.getAddressLatitude(),
							candidate.getAddressLongitude()
						))
						.collect(Collectors.toList());

					// 투표 여부와 함께 정보 반환
					return PlaceVoteStatusResponse.from(true, allPlaceCandidates, userVotedCandidates);
				} else {
					// 투표방이 존재하지만 사용자가 투표하지 않은 경우
					return PlaceVoteStatusResponse.from(false, null, null);
				}
			})
			.orElseGet(() -> PlaceVoteStatusResponse.from(false, null, null));
	}
}
