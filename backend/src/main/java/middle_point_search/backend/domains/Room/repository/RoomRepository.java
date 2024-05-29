package middle_point_search.backend.domains.Room.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import middle_point_search.backend.domains.Room.domain.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {

	boolean existsByIdentityNumber(String identityNumber);
}
