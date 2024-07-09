package middle_point_search.backend.domains.place.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.util.MemberLoader;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.place.domain.Place;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlaceSaveRequest;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlacesFindResponse;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlacesSaveBySelfRequest;
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

		Optional<Place> place = placeRepository.findByRoomAndMember(room, member);

		//1. 기존에 존재하는 데이터가 없으면 저장
		//2. 기존에 존재하는 데이터가 있으면 변경
		if (place.isEmpty()) {
			placeRepository.save(Place.from(placeSaveRequest, room, member));
		} else {
			place.get().update(placeSaveRequest);
		}
	}

	@Transactional(readOnly = false)
	public void savePlacesBySelf(PlacesSaveBySelfRequest request) {
		Room room = memberLoader.getRoom();
		String roomId = memberLoader.getRoomId();

		List<PlaceSaveRequest> placesSaveBySelfRequests = request.getAddresses();
		List<Place> places = placesSaveBySelfRequests.stream()
			.map(placeSaveRequest -> Place.from(placeSaveRequest, room))
			.toList();

		Boolean existence = placeRepository.existsByRoom_IdentityNumber(roomId);

		if (existence) {
			placeRepository.saveAll(places);
		} else {
			updatePlaces(roomId, places);
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
	public void updatePlaces(String roomId, List<Place> places) {
		placeRepository.deleteAllByRoom_IdentityNumber(roomId);

		placeRepository.saveAll(places);
	}
}
