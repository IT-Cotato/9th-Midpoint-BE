package middle_point_search.backend.domains.place.service;

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

		Place place = Place.from(placeSaveRequest, room, member);
		placeRepository.save(place);
	}
}
