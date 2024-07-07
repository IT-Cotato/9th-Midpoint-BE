package middle_point_search.backend.domains.room.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import middle_point_search.backend.domains.room.domain.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {

	boolean existsByIdentityNumber(String identityNumber);

	Optional<Room> findRoomByIdentityNumber(String identityNumber);
	Optional<Room> findByIdentityNumber(String identityNumber);
}
