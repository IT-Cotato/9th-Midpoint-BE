package middle_point_search.backend.domains.timeVoteRoom.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetingDate {
	@Id
	@Column(name = "meeting_date_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "time_vote_room_id")
	private TimeVoteRoom timeVoteRoom;

	@Column(name = "date")
	private LocalDate date;

	public MeetingDate(TimeVoteRoom timeVoteRoom, LocalDate date) {
		this.timeVoteRoom = timeVoteRoom;
		this.date = date;
	}
}
