package middle_point_search.backend.domains.market.service;

import static middle_point_search.backend.common.exception.errorCode.CommonErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.common.util.ExcelUtil;
import middle_point_search.backend.domains.market.dto.dto.StationDto;

@Service
@RequiredArgsConstructor
public class StationService {

	// 모든 지하철역 위치 알아오기
	public List<StationDto> getAllStations() {
		// 역 데이터 위치
		final String stationFilePath = "src/main/resources/static/stationData/stations_2025_03_30.xlsx";

		List<StationDto> stations;
		try {
			stations = ExcelUtil.parseExcelFile(stationFilePath)
				.stream()
				.map(row -> {
					String name = row.get("Station Name (Korean)");
					double latitude = Double.parseDouble(row.get("Latitude"));
					double longitude = Double.parseDouble(row.get("Longitude"));
					String address = row.get("Address");

					return new StationDto(name, latitude, longitude, address);
				})
				.distinct()
				.toList();

		} catch (Exception e) {
			throw CustomException.from(FILE_IO_ERROR);
		}

		return stations;
	}
}
