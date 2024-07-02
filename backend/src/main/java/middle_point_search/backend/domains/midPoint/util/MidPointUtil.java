package middle_point_search.backend.domains.midPoint.util;

import java.util.List;

import middle_point_search.backend.domains.midPoint.dto.MidPointDTO.AddressDTO;
import middle_point_search.backend.domains.midPoint.dto.MidPointDTO.MidPointsFindResponse;

public interface MidPointUtil {

	List<MidPointsFindResponse> findMidPoints(List<AddressDTO> addresses);
}
