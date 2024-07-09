package middle_point_search.backend.domains.place.service;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.common.util.MemberLoader;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.place.domain.Place;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlaceSaveRequest;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlacesFindResponse;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlacesSaveBySelfRequest;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlacesUpdateRequest;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlacesUpdateRequest.PlaceUpdateRequest;
import middle_point_search.backend.domains.place.repository.PlaceRepository;
import middle_point_search.backend.domains.room.domain.Room;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

	private final MemberLoader memberLoader;
	private final PlaceRepository placeRepository;

	@Transactional(readOnly = false)
	public void savePlace(PlaceSaveRequest placeSaveRequest) {

		Room room = memberLoader.getRoom();
		Member member = memberLoader.getMember();

		Boolean existence = placeRepository.existsByRoom_IdentityNumberAndMember_Name(room.getIdentityNumber(),
			member.getName());

		//1. 기존에 존재하는 데이터가 없으면 저장
		//2. 기존에 존재하는 데이터가 있으면 에러
		if (!existence) {
			placeRepository.save(Place.from(placeSaveRequest, room, member));
		} else {
			throw new CustomException(PLACE_CONFLICT);
		}
	}

	@Transactional(readOnly = false)
	public void savePlacesBySelf(PlacesSaveBySelfRequest request) {
		Room room = memberLoader.getRoom();
		String roomId = memberLoader.getRoomId();

		Boolean existence = placeRepository.existsByRoom_IdentityNumber(roomId);

		//1. 장소가 존재하지 않으면 저장
		//2. 장소가 저장하면 중복 에러
		if (!existence) {
			List<PlaceSaveRequest> placesSaveBySelfRequests = request.getAddresses();
			List<Place> places = placesSaveBySelfRequests.stream()
				.map(placeSaveRequest -> Place.from(placeSaveRequest, room))
				.toList();

			placeRepository.saveAll(places);
		} else {
			throw new CustomException(PLACE_CONFLICT);
		}
	}

	public List<PlacesFindResponse> findPlaces(String roomId) {

		List<Place> places = placeRepository.findAllByRoom_IdentityNumber(roomId);

		return places.stream()
			.map(PlacesFindResponse::from)
			.toList();
	}

	// Places 업데이트 하기, 여러 개이므로 다 삭제하고 다시 생성한다.
	@Transactional(readOnly = true)
	public void updatePlaces(PlacesUpdateRequest request) {
		List<PlaceUpdateRequest> placeUpdateRequests = request.getPlaces();

		placeUpdateRequests.stream()
			.forEach(placeUpdateRequest -> {
				Long id = placeUpdateRequest.getPlaceId();

		placeRepository.saveAll(places);
	}

	public void deletePlace(Long placeId) {

		placeRepository.deleteById(placeId);
	}
}
