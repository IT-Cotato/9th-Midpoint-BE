package middle_point_search.backend.domains.TimeVoteRoom.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeVote {
    @Id
    @Column(name = "time_vote_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "time_vote_room_id")
    private TimeVoteRoom timeVoteRoom;

    private Long memberId;

    @ManyToOne
    @JoinColumn(name = "meeting_date_id")
    private MeetingDate meetingDate;

    private LocalDateTime memberAvailableStartTime;

    private LocalDateTime memberAvailableEndTime;


}
