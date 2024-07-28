package middle_point_search.backend.domains.place.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.place.domain.Place;
import middle_point_search.backend.domains.room.domain.Room;

public interface PlaceRepository extends JpaRepository<Place, Long> {

	Optional<Place> findByRoomAndMember(Room room, Member member);

	List<Place>	findAllByRoom_IdentityNumber(String roomId);

	Boolean existsByRoom_IdentityNumber(String roomId);

	Boolean existsByRoom_IdentityNumberAndMember_Name(String roomId, String name);
}
