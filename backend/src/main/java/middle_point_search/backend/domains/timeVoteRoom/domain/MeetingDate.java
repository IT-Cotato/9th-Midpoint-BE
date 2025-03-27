package middle_point_search.backend.domains.timeVoteRoom.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


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
