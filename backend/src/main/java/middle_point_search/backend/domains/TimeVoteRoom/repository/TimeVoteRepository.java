package middle_point_search.backend.domains.TimeVoteRoom.repository;

import middle_point_search.backend.domains.TimeVoteRoom.domain.TimeVote;
import middle_point_search.backend.domains.TimeVoteRoom.domain.TimeVoteRoom;
import middle_point_search.backend.domains.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeVoteRepository extends JpaRepository<TimeVote,Long> {
    boolean existsByTimeVoteRoomAndMember(TimeVoteRoom timeVoteRoom, Member member);
}
