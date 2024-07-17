package middle_point_search.backend.domains.TimeVoteRoom.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import middle_point_search.backend.domains.member.domain.Member;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "meeting_date_id")
    private MeetingDate meetingDate;

    private String memberAvailableStartTime;

    private String memberAvailableEndTime;

    public TimeVote(TimeVoteRoom timeVoteRoom, MeetingDate meetingDate, Member member, String memberAvailableStartTime, String memberAvailableEndTime) {
        this.timeVoteRoom = timeVoteRoom;
        this.meetingDate =meetingDate;
        this.member =member;
        this.memberAvailableStartTime = memberAvailableStartTime;
        this.memberAvailableEndTime = memberAvailableEndTime;
    }

}
