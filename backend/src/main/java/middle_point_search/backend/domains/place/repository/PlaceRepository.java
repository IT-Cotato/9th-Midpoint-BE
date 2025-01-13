package middle_point_search.backend.domains.place.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import middle_point_search.backend.domains.place.domain.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {

	List<Place> findAllByRoom_Id(Long roomId);

	@Modifying
	@Query("DELETE FROM Place p WHERE p.id IN :placeIds")
	void deleteAllByIdIn(List<Long> placeIds);

	@Modifying
	@Query("UPDATE Place p " +
		"SET p.siDo = :siDo, " +
		"    p.siGunGu = :siGunGu, " +
		"    p.roadNameAddress = :roadNameAddress, " +
		"    p.addressLatitude = :addressLat, " +
		"    p.addressLongitude = :addressLong " +
		"WHERE p.id = :placeId")
	void updatePlace(
		@Param("placeId") Long placeId,
		@Param("siDo") String siDo,
		@Param("siGunGu") String siGunGu,
		@Param("roadNameAddress") String roadNameAddress,
		@Param("addressLat") Double addressLat,
		@Param("addressLong") Double addressLong);
}
