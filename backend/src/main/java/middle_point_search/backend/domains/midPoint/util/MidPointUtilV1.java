package middle_point_search.backend.domains.midPoint.util;

import static middle_point_search.backend.common.exception.errorCode.UserErrorCode.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.domains.market.domain.Market;
import middle_point_search.backend.domains.market.repository.MarketRepository;
import middle_point_search.backend.domains.midPoint.dto.MidPointDTO.AddressDTO;
import middle_point_search.backend.domains.midPoint.dto.MidPointDTO.CoordinateDTO;
import middle_point_search.backend.domains.midPoint.dto.MidPointDTO.MidPointsFindResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class MidPointUtilV1 implements MidPointUtil {

	private final MarketRepository marketRepository;
	private final int NUMBER_OF_RESULT = 5;

	@Override
	public List<MidPointsFindResponse> findMidPoints(List<AddressDTO> addresses) {
		// address가 없을 경우
		if (addresses.isEmpty()) {
			throw new CustomException(PLACE_NOT_FOUND);
		}

		List<CoordinateDTO> coordinates = addresses.stream()
			.map(address -> new CoordinateDTO(address.getAddressLong(), address.getAddressLat()))
			.collect(Collectors.toList());

		CoordinateDTO coordinate = calcMidPointCoordinate(coordinates);

		return findMidPointsByCoordinate(coordinate);
	}

	// 주어진 중간 위치 좌표를 통해 추천 장소를 구하는 메서드
	private List<MidPointsFindResponse> findMidPointsByCoordinate(CoordinateDTO mid) {
		double midX = mid.getX();
		double midY = mid.getY();

		List<Market> markets = marketRepository.findAll();

		markets.sort((o1, o2) -> {
			double x1 = o1.getAddressLongitude();
			double y1 = o1.getAddressLatitude();
			double x2 = o2.getAddressLongitude();
			double y2 = o2.getAddressLatitude();

			double dist1 = calcDist(midX, midY, x1, y1);
			double dist2 = calcDist(midX, midY, x2, y2);

			return (int)(dist1 - dist2);
		});

		List<MidPointsFindResponse> responses = new ArrayList<>();

		for (int i = 0; i < NUMBER_OF_RESULT; i++) {
			responses.add(MidPointsFindResponse.from(markets.get(i)));
		}

		return responses;
	}

	// 여러 좌표의 무게 중심 구하는 메서드
	private CoordinateDTO calcMidPointCoordinate(List<CoordinateDTO> coordinates) {

		List<CoordinateDTO> borderCoordinates = grahamScan(coordinates);

		double count = borderCoordinates.size();
		double sumX = 0;
		double sumY = 0;

		for (CoordinateDTO coordinate : borderCoordinates) {
			sumX += coordinate.getX();
			sumY += coordinate.getY();
		}

		return new CoordinateDTO(sumX / count, sumY / count);
	}

	// 두 벡터의 외적을 통해 시계, 반시계 방향을 구하는 ccw 알고리즘
	// CA X AB 이다.
	// ccw => 1: 반시계, 0: 일직선, -1: 시계
	private int ccw(CoordinateDTO a, CoordinateDTO b, CoordinateDTO c) {
		double ccwR = (b.getX() - a.getX()) * (c.getY() - a.getX()) - (c.getX() - a.getX()) * (b.getY() - a.getY());

		if (ccwR > 0) {
			return 1;
		} else if (ccwR < 0) {
			return -1;
		} else {
			return 0;
		}
	}

	// 그라함 스캔 알고리즘, 테두리 좌표들만 구하기
	private List<CoordinateDTO> grahamScan(List<CoordinateDTO> coordinates) {
		// 정렬의 기준이 될 좌표
		CoordinateDTO standard = findStandardPoint(coordinates);

		// 좌표 정렬
		coordinates.sort((o1, o2) -> {
			int ccwR = ccw(standard, o1, o2);

			if (ccwR > 0) { //반시계방향
				return -1;
			} else if (ccwR < 0) { //시계방향
				return 1;
			} else { //거리가 더 가까운 것을 앞 쪽으로
				double dist1 = standard.calcDist(o1);
				double dist2 = standard.calcDist(o2);
				if (dist1 > dist2) {
					return 1;
				} else {
					return -1;
				}
			}
		});

		Stack<CoordinateDTO> S = new Stack<>();
		S.add(standard);

		for (int i = 1; i < coordinates.size(); i++) {
			//시계 방향이면 제거 한다.
			while (S.size() > 1 && ccw(S.get(S.size() - 2), S.get(S.size() - 1), coordinates.get(i)) <= 0) {
				S.pop();
			}
			S.add(coordinates.get(i));
		}

		return S.stream().toList();
	}

	// 기준 좌표 구하기
	private CoordinateDTO findStandardPoint(List<CoordinateDTO> coordinateDTOs) {
		CoordinateDTO first = coordinateDTOs.get(0);
		double firstX = first.getX();
		double firstY = first.getY();

		for (int i = 1; i < coordinateDTOs.size(); i++) {
			CoordinateDTO coordinateDTO = coordinateDTOs.get(i);
			double x = coordinateDTO.getX();
			double y = coordinateDTO.getY();

			// y 좌표가 가장 작은 좌표를 찾는다.
			// y가 같은 경우 x가 더 작은 것을 선택한다.
			if (y < firstY) {
				first = coordinateDTO;
			} else if (y == firstY) {
				if (x < firstX) {
					first = coordinateDTO;
				}
			}
		}

		return first;
	}

	// 두 점 사이의 거리 제곱을 리턴하는 함수
	public Double calcDist(double x1, double y1, double x2, double y2) {
		return Math.pow(y2 - y1, 2D) + Math.pow(x2 - x1, 2D);
	}
}
