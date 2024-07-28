package middle_point_search.backend.domains.place.service;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.place.domain.Place;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlaceSaveRequest;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlacesFindResponse;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlacesSaveBySelfRequest;
import middle_point_search.backend.domains.place.repository.PlaceRepository;
import middle_point_search.backend.domains.room.domain.Room;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

	private final PlaceRepository placeRepository;

	@Transactional
	public void saveOrUpdatePlace(Room room, Member member, PlaceSaveRequest placeSaveRequest) {

		Boolean existence = placeRepository.existsByRoom_IdentityNumberAndMember_Name(room.getIdentityNumber(),
			member.getName());

		//1. 기존에 존재하는 데이터가 없으면 저장
		//2. 기존에 존재하는 데이터가 있으면 변경
		if (!existence) {
			placeRepository.save(Place.from(placeSaveRequest, room, member));
		} else {
			Place place = placeRepository.findByRoomAndMember(room, member)
				.orElseThrow(() -> new CustomException(PLACE_NOT_FOUND));

			place.update(placeSaveRequest);
		}
	}

	@Transactional
	public void savePlacesBySelf(Room room, PlacesSaveBySelfRequest request) {

		String roomId = room.getIdentityNumber();

		Boolean existence = placeRepository.existsByRoom_IdentityNumber(roomId);

		//1. 기존에 존재하는 데이터가 없으면 저장
		//2. 기존에 존재하는 데이터가 있으면 변경
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

		return places.stream().map(PlacesFindResponse::from).toList();
	}
}
