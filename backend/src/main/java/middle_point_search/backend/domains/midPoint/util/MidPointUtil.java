package middle_point_search.backend.domains.midPoint.util;

import java.util.List;

import middle_point_search.backend.domains.midPoint.dto.MidPointDTO.AddressDTO;
import middle_point_search.backend.domains.midPoint.dto.MidPointDTO.FindMidPointsResponse;

public interface MidPointUtil {

	List<FindMidPointsResponse> findMidPoints(List<AddressDTO> addresses);
}
