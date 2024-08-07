package middle_point_search.backend.domains.timeVoteRoom.repository;

import middle_point_search.backend.domains.timeVoteRoom.domain.MeetingDate;
import middle_point_search.backend.domains.timeVoteRoom.domain.TimeVote;
import middle_point_search.backend.domains.timeVoteRoom.domain.TimeVoteRoom;
import middle_point_search.backend.domains.member.domain.Member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimeVoteRepository extends JpaRepository<TimeVote, Long> {
	boolean existsByTimeVoteRoomAndMember(TimeVoteRoom timeVoteRoom, Member member);

	List<TimeVote> findByTimeVoteRoomAndMember(TimeVoteRoom timeVoteRoom, Member member);

	List<TimeVote> findByTimeVoteRoomAndMeetingDate(TimeVoteRoom timeVoteRoom, MeetingDate meetingDate);

	List<TimeVote> findDistinctByTimeVoteRoom(TimeVoteRoom timeVoteRoom);
}

