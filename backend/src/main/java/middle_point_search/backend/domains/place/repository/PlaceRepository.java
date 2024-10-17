package middle_point_search.backend.domains.place.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import middle_point_search.backend.domains.place.domain.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {

	List<Place> findAllByRoom_Id(Long roomId);
}
