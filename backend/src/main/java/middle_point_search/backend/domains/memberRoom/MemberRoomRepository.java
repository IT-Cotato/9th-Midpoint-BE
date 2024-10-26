package middle_point_search.backend.domains.memberRoom;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import middle_point_search.backend.domains.member.domain.Member;

public interface MemberRoomRepository extends JpaRepository<MemberRoom, Long> {

	Boolean existsByMember_IdAndRoom_Id(Long memberId, Long roomId);

	List<MemberRoom> findByMember(Member member);
}
