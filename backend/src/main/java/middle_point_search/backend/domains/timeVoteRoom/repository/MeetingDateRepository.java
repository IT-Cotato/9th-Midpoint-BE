package middle_point_search.backend.domains.timeVoteRoom.repository;

import middle_point_search.backend.domains.timeVoteRoom.domain.MeetingDate;
import middle_point_search.backend.domains.timeVoteRoom.domain.TimeVoteRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MeetingDateRepository extends JpaRepository<MeetingDate, Long> {
    Optional<MeetingDate> findByTimeVoteRoomAndDate(TimeVoteRoom timeVoteRoom, LocalDate date);
    List<MeetingDate> findByTimeVoteRoom(TimeVoteRoom timeVoteRoom);
}
