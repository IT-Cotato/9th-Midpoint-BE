package middle_point_search.backend.common.dummy;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.dummy.dto.MemberDummyDto;
import middle_point_search.backend.common.dummy.dto.MemberRoomDummyDto;
import middle_point_search.backend.common.dummy.dto.PlaceDummyDto;
import middle_point_search.backend.common.dummy.dto.PlaceVoteCandidateDummyDto;
import middle_point_search.backend.common.dummy.dto.PlaceVoteRoomDummyDto;
import middle_point_search.backend.common.dummy.dto.RoomDummyDto;
import middle_point_search.backend.common.dummy.repository.JDBCRepository;
import middle_point_search.backend.domains.member.domain.Role;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataInitService {

	private final PasswordEncoder passwordEncoder;
	private final JDBCRepository jdbcRepository;

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
		// Places 생성
		List<PlaceDummyDto> places = new ArrayList<>();
		for (int i = 1; i <= DummyDataConstant.PLACE_COUNT.count; i++) {
			PlaceDummyDto place = new PlaceDummyDto(
				"siDo" + i,
				"siGunGu" + i,
				"roadNameAddress" + i,
				37.0 + i * 0.0001,
				127.0 + i * 0.0001,
				String.valueOf(i), // roomId
				(long)i, // memberId
				"googlePlaceId" + i
			);
			places.add(place);
		}
		jdbcRepository.saveAllPlaces(places);
	}

	// PlaceVoteRoom 더미데이터 초기화
	public void initializePlaceVoteRoomData() {
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
	public void initializePlaceVoteCandidateData() {
		// PlaceVoteCandidates 생성
		List<PlaceVoteCandidateDummyDto> placeVoteCandidates = new ArrayList<>();
		for (int i = 1; i <= DummyDataConstant.PLACE_VOTE_CANDIDATE_COUNT.count; i++) {
			PlaceVoteCandidateDummyDto placeVoteCandidate = new PlaceVoteCandidateDummyDto(
				"placeVoteCandidate" + i,
				"siDo" + i,
				"siGunGu" + i,
				"roadNameAddress" + i,
				37.0 + i * 0.0001,
				127.0 + i * 0.0001,
				(long)i // placeVoteRoomId
			);
			placeVoteCandidates.add(placeVoteCandidate);
		}
		jdbcRepository.saveAllPlaceVoteCandidates(placeVoteCandidates);
	}
}
