package middle_point_search.backend.domains.place.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import middle_point_search.backend.domains.place.domain.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {

	List<Place> findAllByRoom_Id(String roomId);

	void deleteByIdAndRoom_Id(Long placeId, String roomId);

	@Modifying
	@Query("UPDATE Place p " +
		"SET p.member.id = :memberId, " +
		"    p.siDo = :siDo, " +
		"    p.siGunGu = :siGunGu, " +
		"    p.roadNameAddress = :roadNameAddress, " +
		"    p.addressLatitude = :addressLat, " +
		"    p.addressLongitude = :addressLong, " +
		"    p.googlePlaceId = :googlePlaceId " +
		"WHERE p.id = :placeId")
	void updatePlace(
		@Param("memberId") Long memberId,
		@Param("placeId") Long placeId,
		@Param("googlePlaceId") String googlePlaceId,
		@Param("siDo") String siDo,
		@Param("siGunGu") String siGunGu,
		@Param("roadNameAddress") String roadNameAddress,
		@Param("addressLat") Double addressLat,
		@Param("addressLong") Double addressLong);
}
