package middle_point_search.backend.domains.place.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.util.MemberLoader;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.place.domain.Place;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlaceSaveRequest;
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
}
