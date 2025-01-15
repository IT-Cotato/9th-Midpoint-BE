package middle_point_search.backend.domains.midPoint.service;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.domains.google.dto.DistanceMatrixResponse;
import middle_point_search.backend.domains.google.service.GoogleService;
import middle_point_search.backend.domains.memberRoom.service.MemberRoomValidateService;
import middle_point_search.backend.domains.midPoint.dto.MidPointDTO.AddressDTO;
import middle_point_search.backend.domains.midPoint.dto.MidPointDTO.MidPointsFindResponse;
import middle_point_search.backend.domains.midPoint.dto.MidPointDTO.TravelTimesFindResponse;
import middle_point_search.backend.domains.midPoint.util.MidPointUtil;
import middle_point_search.backend.domains.place.domain.Place;
import middle_point_search.backend.domains.place.repository.PlaceRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MidPointService {

	private final MidPointUtil midPointUtil;
	private final PlaceRepository placeRepository;
	private final MemberRoomValidateService memberRoomValidateService;
	private final GoogleService googleService;

	// 주어진 주소들로 중간 장소 리스트를 조회하는 메서드
	public List<MidPointsFindResponse> findMidPoints(List<AddressDTO> addressDTOs) {
		return midPointUtil.findMidPoints(addressDTOs);
	}

	// 주어진 RoomId로 중간 장소 리스트를 조회하는 메서드
	public List<MidPointsFindResponse> findMidPointsByRoomId(Long memberId, String roomId) {
		// 회원이 방에 속해있는지 확인
		memberRoomValidateService.validateAuthorizedMember(memberId, roomId);

		List<Place> places = placeRepository.findAllByRoom_Id(roomId);
		List<AddressDTO> addressDTOs = places.stream()
			.map(AddressDTO::from)
			.toList();

		return findMidPoints(addressDTOs);
	}

	// 방 장소들의 중간지점까지의 이동시간을 조회하는 메서드
	public TravelTimesFindResponse findTravelTimes(String roomId, Long memberId, Double latitude, Double longitude) {
		// 회원이 방에 속해있는지 확인
		memberRoomValidateService.validateAuthorizedMember(memberId, roomId);

		// 장소 조회 및 검증
		List<Place> places = placeRepository.findAllByRoom_Id(roomId);
		validateOriginPlacesExist(places);

		// 방에 속한 장소들의 좌표를 가져옴
		List<String> originPlaceIds = places.stream()
			.map(Place::getGooglePlaceId)
			.toList();

		// 목적지 placeId 조회
		String destinationPlaceId = googleService.findGooglePlaceId(latitude, longitude);

		// Google API 호출 및 결과 처리
		DistanceMatrixResponse distanceMatrixResponse = googleService.findTravelTimes(destinationPlaceId, originPlaceIds);

		// 응답 생성
		List<TravelTimesFindResponse.Element> elements = createTravelTimeElements(places, distanceMatrixResponse);

		return TravelTimesFindResponse.from(elements);
	}

	// 방에 속한 장소가 없을 때 예외처리
	private void validateOriginPlacesExist(List<Place> places) {
		if (places.isEmpty()) {
			throw CustomException.from(PLACE_NOT_FOUND);
		}
	}

	// 이동 시간 요소 생성 로직
	private List<TravelTimesFindResponse.Element> createTravelTimeElements(List<Place> places, DistanceMatrixResponse response) {
		return IntStream.range(0, places.size())
			.mapToObj(i -> {
				Place place = places.get(i);
				DistanceMatrixResponse.Element responseElement = response.getRows().get(i).getElements().get(0);

				if ("ZERO_RESULTS".equals(responseElement.getStatus())) {
					return TravelTimesFindResponse.Element.noContent(place.getId());
				}

				return TravelTimesFindResponse.Element.from(
					place.getId(),
					responseElement.getDuration().getText(),
					responseElement.getDuration().getValue(),
					responseElement.getDistance().getText(),
					responseElement.getDistance().getValue()
				);
			})
			.toList(); // 불변 리스트 생성
	}
}
