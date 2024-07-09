package middle_point_search.backend.domains.place.service;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.place.domain.Place;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlaceSaveRequest;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlaceUpdateRequest;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlacesFindResponse;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlacesSaveBySelfRequest;
import middle_point_search.backend.domains.place.repository.PlaceRepository;
import middle_point_search.backend.domains.room.domain.Room;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

	private final PlaceRepository placeRepository;

	@Transactional(readOnly = false)
	public void savePlace(Room room, Member member, PlaceSaveRequest placeSaveRequest) {

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
	public void savePlacesBySelf(Room room, PlacesSaveBySelfRequest request) {

		String roomId = room.getIdentityNumber();

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

	// Place 업데이트 하기
	@Transactional(readOnly = false)
	public void updatePlaces(Long placeId, PlaceUpdateRequest request) {

		Place place = placeRepository.findById(placeId)
			.orElseThrow(() -> new CustomException(PLACE_NOT_FOUND));

		place.update(request);
	}

	// Place 삭제하기
	@Transactional(readOnly = false)
	public void deletePlace(Long placeId) {

		placeRepository.deleteById(placeId);
	}
}
