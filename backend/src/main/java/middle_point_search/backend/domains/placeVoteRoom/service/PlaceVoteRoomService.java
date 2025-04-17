package middle_point_search.backend.domains.placeVoteRoom.service;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.domains.memberRoom.service.MemberRoomValidateService;
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteCandidate;
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteRoom;
import middle_point_search.backend.domains.placeVoteRoom.dto.request.CreatePlaceVoteRoomRequest;
import middle_point_search.backend.domains.placeVoteRoom.dto.request.UpdatePlaceVoteRoomRequest;
import middle_point_search.backend.domains.placeVoteRoom.dto.response.CreatePlaceVoteRoomResponse;
import middle_point_search.backend.domains.placeVoteRoom.dto.response.FindPlaceVoteCandidatesResponse;
import middle_point_search.backend.domains.placeVoteRoom.repository.PlaceVoteCandidateRepository;
import middle_point_search.backend.domains.placeVoteRoom.repository.PlaceVoteRepository;
import middle_point_search.backend.domains.placeVoteRoom.repository.PlaceVoteRoomRepository;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.repository.RoomRepository;
import middle_point_search.backend.domains.room.service.RoomValidationService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceVoteRoomService {

	private final PlaceVoteRoomRepository placeVoteRoomRepository;
	private final RoomRepository roomRepository;
	private final MemberRoomValidateService memberRoomValidateService;
	private final PlaceVoteRepository placeVoteRepository;
	private final PlaceVoteCandidateRepository placeVoteCandidateRepository;
	private final RoomValidationService roomValidationService;

	// 장소투표방 생성
	@Transactional(rollbackFor = {CustomException.class})
	public CreatePlaceVoteRoomResponse createPlaceVoteRoom(
		Long memberId,
		String roomId,
		CreatePlaceVoteRoomRequest request
	) {
		roomValidationService.validateRoomExisting(roomId);
		memberRoomValidateService.validateAuthorizedMember(memberId, roomId);
		validateDuplicatePlaceVoteRoom(roomId);

		Room room = roomRepository.findById(roomId)
			.orElseThrow(() -> CustomException.from(ROOM_NOT_FOUND));

		// 장소투표방 엔티티 생성 및 저장
		PlaceVoteRoom placeVoteRoom = placeVoteRoomRepository.save(new PlaceVoteRoom(room));
		request.placeCandidates().stream()
			.map(placeCandidateInfo -> new PlaceVoteCandidate(placeCandidateInfo, placeVoteRoom))
			.forEach(placeVoteCandidateRepository::save);

		return CreatePlaceVoteRoomResponse.from(placeVoteRoom.getId());
	}

	//장소투표방 리셋
	@Transactional(rollbackFor = {CustomException.class})
	public void UpdatePlaceVoteRoom(Long memberId, String roomId, UpdatePlaceVoteRoomRequest request) {
		memberRoomValidateService.validateAuthorizedMember(memberId, roomId);

		PlaceVoteRoom placeVoteRoom = placeVoteRoomRepository.findByRoom_Id(roomId)
			.orElseThrow(() -> CustomException.from(PLACE_VOTE_ROOM_NOT_FOUND));

		// 장소투표방 리셋
		placeVoteCandidateRepository.deleteAllByPlaceVoteRoom(placeVoteRoom);

		// 장소투표 후보 추가
		request.placeCandidates().stream()
			.map(placeCandidateInfo -> new PlaceVoteCandidate(placeCandidateInfo, placeVoteRoom))
			.forEach(placeVoteCandidateRepository::save);
	}

	// 장소투표방 조회
	public Optional<PlaceVoteRoom> findByRoomId(String roomId) {
		return placeVoteRoomRepository.findByRoom_Id(roomId);
	}

	// 장소투표방 중복 체크
	private void validateDuplicatePlaceVoteRoom(String roomId) {
		boolean exists = placeVoteRoomRepository.existsByRoom_Id(roomId);
		if (exists) {
			throw CustomException.from(DUPLICATE_VOTE_ROOM);
		}
	}

	// 장소투표방 존재 여부 확인, 존재시 true, 존재하지 않을시 false 반환
	public FindPlaceVoteCandidatesResponse findPlaceVoteCandidates(Long memberId, String roomId) {
		memberRoomValidateService.validateAuthorizedMember(memberId, roomId);

		Optional<PlaceVoteRoom> placeVoteRoomOptional = placeVoteRoomRepository.findByRoom_Id(roomId);

		return placeVoteRoomOptional
			.map(placeVoteRoom -> {
				List<FindPlaceVoteCandidatesResponse.PlaceCandidateDto> placeCandidateDtos = placeVoteCandidateRepository
					.findAllByPlaceVoteRoom(placeVoteRoom)
					.stream()
					.map(FindPlaceVoteCandidatesResponse.PlaceCandidateDto::from)
					.collect(Collectors.toList());

				return FindPlaceVoteCandidatesResponse.from(true, placeCandidateDtos);
			})
			.orElseGet(() -> FindPlaceVoteCandidatesResponse.from(false, null));
	}

	// 장소 투표방 삭제
	@Transactional(rollbackFor = {CustomException.class})
	public void deletePlaceVoteRoomAndAssociatedEntities(String roomId) {
		placeVoteRoomRepository.findByRoom_Id(roomId)
			.ifPresent(placeVoteRoom -> {
				// 장소 투표 방과 관련된 투표, 투표 후보, 투표 방 삭제
				placeVoteRepository.deleteAllByPlaceVoteRoom(placeVoteRoom);
				placeVoteCandidateRepository.deleteAllByPlaceVoteRoom(placeVoteRoom);
				placeVoteRoomRepository.delete(placeVoteRoom);
			});
	}
}