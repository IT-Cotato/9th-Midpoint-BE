package middle_point_search.backend.domains.market.service;

import static middle_point_search.backend.common.exception.errorCode.CommonErrorCode.*;

import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.common.util.ExcelUtil;
import middle_point_search.backend.domains.market.dto.dto.StationDto;

@Service
@RequiredArgsConstructor
public class StationService {

	public List<StationDto> getAllStations() {
		try {
			// JAR 내 리소스 접근
			ClassPathResource resource = new ClassPathResource("static/stationData/stations_2025_03_30.xlsx");

			List<StationDto> stations = ExcelUtil.parseExcelFile(resource.getInputStream())
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

			return stations;
		} catch (Exception e) {
			throw CustomException.from(FILE_IO_ERROR);
		}
	}
}
