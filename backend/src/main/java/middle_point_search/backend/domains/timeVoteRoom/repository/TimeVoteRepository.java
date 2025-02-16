package middle_point_search.backend.domains.timeVoteRoom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.timeVoteRoom.domain.MeetingDate;
import middle_point_search.backend.domains.timeVoteRoom.domain.TimeVote;
import middle_point_search.backend.domains.timeVoteRoom.domain.TimeVoteRoom;

public interface TimeVoteRepository extends JpaRepository<TimeVote, Long> {
	boolean existsByTimeVoteRoomAndMember(TimeVoteRoom timeVoteRoom, Member member);

	List<TimeVote> findAllByTimeVoteRoomAndMember(TimeVoteRoom timeVoteRoom, Member member);

	List<TimeVote> findAllByTimeVoteRoomAndMeetingDate(TimeVoteRoom timeVoteRoom, MeetingDate meetingDate);

	List<TimeVote> findDistinctByTimeVoteRoom(TimeVoteRoom timeVoteRoom);

	@Query("select tv from TimeVote tv where tv.timeVoteRoom = :timeVoteRoom and tv.meetingDate = :meetingDate and tv.member != :member")
	List<TimeVote> findAllByTimeVoteRoomAndMeetingDateExceptMember(TimeVoteRoom timeVoteRoom, MeetingDate meetingDate, Member member);

	void deleteAllByMemberId(Long memberId);
}

