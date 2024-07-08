package middle_point_search.backend.domains.TimeVoteRoom.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import middle_point_search.backend.domains.room.domain.Room;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeVoteRoom {

    @Id
    @Column(name = "time_vote_room_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @OneToMany(mappedBy = "timeVoteRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TimeVote> timeVotes;

    @OneToMany(mappedBy = "timeVoteRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MeetingDate> meetingDates;

    @Column(name = "meeting_name")
    private String meetingName;

    @Column(name = "url")
    private String url;

    public TimeVoteRoom(Room room, String meetingName) {
        this.room = room;
        this.meetingName = meetingName;
    }
    public void setMeetingDates(List<LocalDate> dates) {
        // MeetingDate 엔티티를 생성하여 연관 관계 설정
        this.meetingDates = dates.stream()
                .map(date -> new MeetingDate(this, date))
                .collect(Collectors.toList());
    }
    public void setUrl(String url) {
        if (this.url == null) {
            this.url = url;
        }
    }


}



