package middle_point_search.backend.domains.midPoint.util;

import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.domains.midPoint.dto.MidPointDTO.AddressDTO;
import middle_point_search.backend.domains.midPoint.dto.MidPointDTO.CoordinateDTO;

@Slf4j
@Component
public class MidPointUtilV1 implements MidPointUtil {

	@Override
	public List<AddressDTO> findMidPoints(List<AddressDTO> addresses) {
		List<CoordinateDTO> coordinates = addresses.stream()
			.map(address -> new CoordinateDTO(address.getAddressLong(), address.getAddressLat()))
			.collect(Collectors.toList());

		CoordinateDTO coordinate = calcMidPointCoordinate(coordinates);

		return null;
	}

	private CoordinateDTO calcMidPointCoordinate(List<CoordinateDTO> coordinates) {
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

		List<CoordinateDTO> borderCoordinates = grahamScan(coordinates, standard);

		for (CoordinateDTO c : borderCoordinates) {
			log.info("x : {}, y : {}", c.getX(), c.getY());
		}

		return null;
	}

	private List<AddressDTO> findMidPointsByCoordinate(CoordinateDTO coordinateDTO) {
		return null;
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
	private List<CoordinateDTO> grahamScan(List<CoordinateDTO> coordinateDTOs, CoordinateDTO standard) {
		Stack<CoordinateDTO> S = new Stack<>();
		S.add(standard);

		for (int i = 1; i < coordinateDTOs.size(); i++) {
			//시계 방향이면 제거 한다.
			while (S.size() > 1 && ccw(S.get(S.size() - 2), S.get(S.size() - 1), coordinateDTOs.get(i)) <= 0) {
				S.pop();
			}
			S.add(coordinateDTOs.get(i));
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
			} else if (y == firstY){
				if (x < firstX) {
					first = coordinateDTO;
				}
			}
		}

		return first;
	}
}
