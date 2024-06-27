package middle_point_search.backend.domains.midPoint.util;

import java.util.List;

import middle_point_search.backend.domains.midPoint.dto.MidPointDTO;
import middle_point_search.backend.domains.midPoint.dto.MidPointDTO.AddressDTO;
import middle_point_search.backend.domains.midPoint.dto.MidPointDTO.CoordinateDTO;

public interface MidPointUtil {

	List<AddressDTO> findMidPoints(List<AddressDTO> addresses);
}
