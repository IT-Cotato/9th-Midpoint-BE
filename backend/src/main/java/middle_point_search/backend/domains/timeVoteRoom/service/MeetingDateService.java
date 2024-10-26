package middle_point_search.backend.domains.timeVoteRoom.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.domains.timeVoteRoom.domain.MeetingDate;
import middle_point_search.backend.domains.timeVoteRoom.domain.TimeVoteRoom;
import middle_point_search.backend.domains.timeVoteRoom.repository.MeetingDateRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingDateService {

	private final MeetingDateRepository meetingDateRepository;

	public Optional<MeetingDate> findByTimeVoteRoomAndDate(TimeVoteRoom timeVoteRoom, LocalDate localDate) {
		return meetingDateRepository.findByTimeVoteRoomAndDate(timeVoteRoom, localDate);
	}

	public List<MeetingDate> findByTimeVoteRoom(TimeVoteRoom timeVoteRoom) {
		return meetingDateRepository.findByTimeVoteRoom(timeVoteRoom);
	}
}
