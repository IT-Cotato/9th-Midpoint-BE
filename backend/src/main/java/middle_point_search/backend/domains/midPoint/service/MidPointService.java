package middle_point_search.backend.domains.midPoint.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.domains.midPoint.dto.MidPointDTO.AddressDTO;
import middle_point_search.backend.domains.midPoint.dto.MidPointDTO.MidPointsFindResponse;
import middle_point_search.backend.domains.midPoint.util.MidPointUtil;
import middle_point_search.backend.domains.place.domain.Place;
import middle_point_search.backend.domains.place.repository.PlaceRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MidPointService {

	private final MidPointUtil midPointUtil;
	private final PlaceRepository placeRepository;

	// 주어진 주소들로 중간 장소 리스트를 조회하는 메서드
	public List<MidPointsFindResponse> findMidPoints(List<AddressDTO> addressDTOs) {
		return midPointUtil.findMidPoints(addressDTOs);
	}

	// 주어진 RoomId로 중간 장소 리스트를 조회하는 메서드
	public List<MidPointsFindResponse> findMidPointsByRoomId(String roomId) {
		List<Place> places = placeRepository.findAllByRoom_IdentityNumber(roomId);
		List<AddressDTO> addressDTOs = places.stream()
			.map(AddressDTO::from)
			.toList();

		return findMidPoints(addressDTOs);
	}
}
