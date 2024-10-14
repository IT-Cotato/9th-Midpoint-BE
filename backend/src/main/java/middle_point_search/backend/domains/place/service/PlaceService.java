package middle_point_search.backend.domains.place.service;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.domain.Role;
import middle_point_search.backend.domains.member.service.MemberService;
import middle_point_search.backend.domains.place.domain.Place;
import middle_point_search.backend.domains.place.dto.PlaceDTO;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlaceFindResponse;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlaceSaveOrUpdateRequest;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlaceVO;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlacesFindResponse;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlacesSaveOrUpdateBySelfRequest;
import middle_point_search.backend.domains.place.repository.PlaceRepository;
import middle_point_search.backend.domains.room.domain.Room;
import middle_point_search.backend.domains.room.domain.RoomType;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

	private final PlaceRepository placeRepository;
	private final MemberService memberService;

	//장소 저장 및 업데이트 하고, Member 및 Room 역할 변경
	@Transactional(rollbackFor = {CustomException.class})
	public void saveOrUpdatePlaceAndRoleUpdate(Room room, Member member, PlaceSaveOrUpdateRequest request) {

		checkRoomType(room.getRoomType(), RoomType.TOGETHER);

		saveOrUpdatePlace(room, member, request);
		memberService.updateMemberRole(member, Role.USER);
	}

	//장소 저장 및 업데이트
	private void saveOrUpdatePlace(Room room, Member member, PlaceSaveOrUpdateRequest request) {

		String roomId = room.getIdentityNumber();
		String name = member.getName();

		Boolean existence = placeRepository.existsByRoom_IdentityNumberAndMember_Name(roomId, name);

		//1. 기존에 존재하는 데이터가 없으면 저장
		//2. 기존에 존재하는 데이터가 있으면 변경
		if (!existence) {
			placeRepository.save(Place.from(request, room, member));
		} else {
			Place place = placeRepository.findByRoomAndMember(room, member)
				.orElseThrow(() -> CustomException.from(PLACE_NOT_FOUND));

			place.update(request);
		}
	}

	//개인이 모든 장소 저장 및 업데이트 하고, Member 및 Room 역할 변경
	@Transactional(rollbackFor = {CustomException.class})
	public void saveOrUpdatePlacesBySelfAndRoleUpdate(Room room, PlacesSaveOrUpdateBySelfRequest request) {

		String roomId = room.getIdentityNumber();

		checkRoomType(room.getRoomType(), RoomType.SELF);

		saveOrUpdatePlacesBySelf(room, request);
		memberService.updateRomeMembersRole(roomId, Role.USER);
	}

	//개인이 모든 장소 저장 및 업데이트
	private void saveOrUpdatePlacesBySelf(Room room, PlacesSaveOrUpdateBySelfRequest request) {

		String roomId = room.getIdentityNumber();

		Boolean existence = placeRepository.existsByRoom_IdentityNumber(roomId);

		// 이미 장소가 존재하면 삭제
		if (existence) {
			placeRepository.deleteAllByRoom_IdentityNumber(roomId);
		}

		// 새로운 장소들 저장
		List<PlaceSaveOrUpdateRequest> placesSaveBySelfRequests = request.getAddresses();
		List<Place> places = placesSaveBySelfRequests.stream()
			.map(placeSaveOrUpdateRequest -> Place.from(placeSaveOrUpdateRequest, room))
			.toList();

		placeRepository.saveAll(places);
	}

	//개개인 장소 조회
	public PlaceFindResponse findPlace(String roomId, String memberName) {

		//내 장소 조회
		PlaceVO myPlace = placeRepository.findByRoom_IdentityNumberAndMember_Name(roomId, memberName)
			.map(Place::toVO)
			.orElse(null);

		//내 장소 존재 유무
		Boolean myPlaceExistence = myPlace != null;

		//다른 사람들 장소 조회
		List<PlaceVO> otherPlaces = placeRepository.findAllByRoom_IdentityNumberAndMember_Name(roomId, memberName)
			.stream().map(Place::toVO)
			.toList();

		//다른 사람들 장소 유무
		boolean otherPlacesExistence = !otherPlaces.isEmpty();

		return new PlaceDTO.PlaceFindResponse(myPlaceExistence, myPlace, otherPlacesExistence, otherPlaces);
	}

	//개인 장소 조회
	public PlacesFindResponse findPlaces(String roomId) {

		List<Place> places = placeRepository.findAllByRoom_IdentityNumber(roomId);

		List<PlaceVO> placeVOs = places.stream()
			.map(Place::toVO)
			.collect(Collectors.toList());

		boolean existence = !placeVOs.isEmpty();
		return new PlacesFindResponse(existence, placeVOs);
	}

	private void checkRoomType(RoomType requestRoomType, RoomType expectationRoomType) {

		if (requestRoomType != expectationRoomType) {
			throw CustomException.from(ROOM_TYPE_UNPROCESSABLE);
		}
	}
}
