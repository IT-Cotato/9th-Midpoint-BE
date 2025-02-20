package middle_point_search.backend.common.dummy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.dummy.dto.MeetingDateDummyDto;
import middle_point_search.backend.common.dummy.dto.MemberDummyDto;
import middle_point_search.backend.common.dummy.dto.MemberRoomDummyDto;
import middle_point_search.backend.common.dummy.dto.PlaceDummyDto;
import middle_point_search.backend.common.dummy.dto.PlaceVoteCandidateDummyDto;
import middle_point_search.backend.common.dummy.dto.PlaceVoteCandidateMemberDummyDto;
import middle_point_search.backend.common.dummy.dto.PlaceVoteRoomDummyDto;
import middle_point_search.backend.common.dummy.dto.RoomDummyDto;
import middle_point_search.backend.common.dummy.dto.TimeVoteDummyDto;
import middle_point_search.backend.common.dummy.dto.TimeVoteRoomDummyDto;
import middle_point_search.backend.common.dummy.repository.JDBCRepository;
import middle_point_search.backend.domains.google.service.GoogleService;
import middle_point_search.backend.domains.member.domain.Role;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataInitService {

	private final PasswordEncoder passwordEncoder;
	private final JDBCRepository jdbcRepository;
	private final GoogleService googleService;

	// 멤버 더미데이터 초기화
	public void initializeMemberData() {
		List<MemberDummyDto> memberDtos = new ArrayList<>();
		String password = passwordEncoder.encode("1234");
		for (int i = 1; i <= DummyDataConstant.MEMBER_ROOM_COUNT.count; i++) {
			MemberDummyDto member = new MemberDummyDto(
				"user" + i + "@test.com",
				password,
				"user" + i,
				Role.USER);
			memberDtos.add(member);
		}
		jdbcRepository.saveAllMembers(memberDtos);
	}

	// 방 더미데이터 초기화
	public void initializeRoomData() {
		// Rooms 생성
		List<RoomDummyDto> rooms = new ArrayList<>();
		for (int i = 1; i <= DummyDataConstant.ROOM_COUNT.count; i++) {
			RoomDummyDto room = new RoomDummyDto(
				String.valueOf(i),
				"room" + i,
				"memo" + i
			);
			rooms.add(room);
		}
		jdbcRepository.saveAllRooms(rooms);
	}

	// MemberRoom 더미데이터 초기화
	public void initializeMemberRoomData() {
		// MemberRooms 생성
		List<MemberRoomDummyDto> memberRooms = new ArrayList<>();
		for (int i = 1; i <= DummyDataConstant.MEMBER_ROOM_COUNT.count; i++) {
			MemberRoomDummyDto memberRoom = new MemberRoomDummyDto(
				(long)i, // memberId
				String.valueOf(i) // roomId
			);
			memberRooms.add(memberRoom);
		}
		jdbcRepository.saveAllMemberRooms(memberRooms);
	}

	// Place 더미데이터 초기화
	public void initializePlaceData() {
		String siDo = "서울특별시";
		String siGunGu = "강남구";
		String roadNameAddress = "테헤란로 521";
		Double latitude = 37.49186244665138;
		Double longitude = 127.00909141903338;
		String googlePlaceId = googleService.findGooglePlaceId(latitude, longitude);

		// Places 생성
		List<PlaceDummyDto> places = new ArrayList<>();
		for (int i = 1; i <= DummyDataConstant.PLACE_COUNT.count; i++) {
			PlaceDummyDto place = new PlaceDummyDto(
				siDo,
				siGunGu,
				roadNameAddress,
				latitude,
				longitude,
				String.valueOf(i), // roomId
				(long)i, // memberId
				googlePlaceId
			);
			places.add(place);
		}
		jdbcRepository.saveAllPlaces(places);
	}

	// PlaceVote 더미데이터 초기화
	public void initializePlaceVote() {
		initializePlaceVoteRoom(); // Room 필요
		initializePlaceVoteCandidateData(); // PlaceVoteRoom 필요
		initializePlaceVoteCandidateMemberData(); // Member, PlaceVoteCandidate 필요
	}

	// PlaceVoteRoom 더미데이터 초기화
	private void initializePlaceVoteRoom() {
		List<PlaceVoteRoomDummyDto> placeVoteRooms = new ArrayList<>();
		for (int i = 1; i <= DummyDataConstant.PLACE_VOTE_ROOM_COUNT.count; i++) {
			PlaceVoteRoomDummyDto placeVoteRoom = new PlaceVoteRoomDummyDto(
				String.valueOf(i) // roomId
			);
			placeVoteRooms.add(placeVoteRoom);
		}
		jdbcRepository.saveAllPlaceVoteRooms(placeVoteRooms);
	}

	// PlaceVoteCandidate 더미데이터 초기화
	private void initializePlaceVoteCandidateData() {
		String siDo = "서울특별시";
		String siGunGu = "마포구";
		String roadNameAddress = "양화로 160";
		Double latitude =  37.55559246073183;
		Double longitude = 126.92274772146207 ;

		// PlaceVoteCandidates 생성
		List<PlaceVoteCandidateDummyDto> placeVoteCandidates = new ArrayList<>();
		for (int i = 1; i <= DummyDataConstant.PLACE_VOTE_CANDIDATE_COUNT.count; i++) {
			PlaceVoteCandidateDummyDto placeVoteCandidate = new PlaceVoteCandidateDummyDto(
				"placeVoteCandidate" + i,
				siDo,
				siGunGu,
				roadNameAddress,
				latitude,
				longitude,
				(long)i // placeVoteRoomId
			);
			placeVoteCandidates.add(placeVoteCandidate);
		}
		jdbcRepository.saveAllPlaceVoteCandidates(placeVoteCandidates);
	}

	// PlaceVoteCandidateMember 더미데이터 초기화
	private void initializePlaceVoteCandidateMemberData() {
		// PlaceVoteCandidateMembers 생성
		List<PlaceVoteCandidateMemberDummyDto>	placeVoteCandidateMembers = new ArrayList<>();
		for (int i = 1; i <= DummyDataConstant.PLACE_VOTE_CANDIDATE_MEMBER_COUNT.count; i++) {
			PlaceVoteCandidateMemberDummyDto placeVoteCandidateMember = new PlaceVoteCandidateMemberDummyDto(
				(long)i, // memberId
				(long)i // placeVoteCandidateId
			);
			placeVoteCandidateMembers.add(placeVoteCandidateMember);
		}
		jdbcRepository.saveAllPlaceVoteCandidateMembers(placeVoteCandidateMembers);
	}

	// TimeVoteRoom, MeetingDate, TimeVote 더미데이터 초기화
	public void initializeTimeVote() {
		LocalDate date = LocalDate.of(2021, 10, 1);
		LocalDateTime start = LocalDateTime.of(2021, 10, 1, 10, 0);
		LocalDateTime end = LocalDateTime.of(2021, 10, 1, 12, 0);

		initializeTimeVoteRoom(); // Room 필요
		initializeMeetingDate(date); // TimeVoteRoom 필요
		initializeTimeVote(start, end); // TimeVoteRoom, MeetingDate, Member 필요
	}

	// TimeVoteRooms 저장
	private void initializeTimeVoteRoom() {
		List<TimeVoteRoomDummyDto> timeVoteRooms = new ArrayList<>();
		for (int i = 1; i <= DummyDataConstant.TIME_VOTE_ROOM_COUNT.count; i++) {
			TimeVoteRoomDummyDto timeVoteRoom = new TimeVoteRoomDummyDto(
				String.valueOf(i) // roomId
			);
			timeVoteRooms.add(timeVoteRoom);
		}
		jdbcRepository.saveAllTimeVoteRooms(timeVoteRooms);
	}

	// MeetingDates 저장
	private void initializeMeetingDate(LocalDate date) {
		List<MeetingDateDummyDto> meetingDates = new ArrayList<>();
		for (int i = 1; i <= DummyDataConstant.MEETING_DATE_COUNT.count; i++) {
			MeetingDateDummyDto meetingDate = new MeetingDateDummyDto(
				(long)i,
				date
			);
			meetingDates.add(meetingDate);
		}
		jdbcRepository.saveAllMeetingDates(meetingDates);
	}

	// TimeVotes 저장
	private void initializeTimeVote(LocalDateTime start, LocalDateTime end) {
		List<TimeVoteDummyDto> timeVotes = new ArrayList<>();
		for (int i = 1; i <= DummyDataConstant.TIME_VOTE_COUNT.count; i++) {
			TimeVoteDummyDto timeVote = new TimeVoteDummyDto(
				(long)i, // timeVoteRoomId
				(long)i, // meetingDateId
				(long)i, // memberId
				start,
				end
			);
			timeVotes.add(timeVote);
		}
		jdbcRepository.saveAllTimeVotes(timeVotes);
	}
}
