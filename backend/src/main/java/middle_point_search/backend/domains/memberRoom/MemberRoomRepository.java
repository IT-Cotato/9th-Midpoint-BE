package middle_point_search.backend.domains.memberRoom;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRoomRepository extends JpaRepository<MemberRoom, Long> {

	Boolean existsByMember_IdAndRoom_Id(Long memberId, String roomId);

	List<MemberRoom> findByMember_Id(Long memberId);
}
