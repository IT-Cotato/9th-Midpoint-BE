package middle_point_search.backend.domains.memberRoom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import middle_point_search.backend.domains.memberRoom.domain.MemberRoom;

public interface MemberRoomRepository extends JpaRepository<MemberRoom, Long> {

	Boolean existsByMember_IdAndRoom_Id(Long memberId, String roomId);

	List<MemberRoom> findByMember_Id(Long memberId);

	List<MemberRoom> findAllByRoomId(String roomId);

	void deleteAllByMemberId(Long memberId);

	void deleteByRoomIdAndMemberId(String roomId, Long memberId);

	boolean existsByRoomId(String roomId);
}
