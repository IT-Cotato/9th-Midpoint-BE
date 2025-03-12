package middle_point_search.backend.domains.place.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdatePlaceRequest(
	@NotNull(message = "placeId는 비어 있을 수 없습니다.") Long placeId,
	@NotBlank(message = "siDo는 비어 있을 수 없습니다.") String siDo,
	@NotBlank(message = "siGunGu는 비어 있을 수 없습니다.") String siGunGu,
	@NotBlank(message = "roadNameAddress는 비어 있을 수 없습니다.") String roadNameAddress,
	@NotNull(message = "addressLat는 비어 있을 수 없습니다.") Double addressLat,
	@NotNull(message = "addressLong는 비어 있을 수 없습니다.") Double addressLong
) {}