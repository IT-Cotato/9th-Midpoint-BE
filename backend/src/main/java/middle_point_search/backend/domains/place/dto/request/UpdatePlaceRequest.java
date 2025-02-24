package middle_point_search.backend.domains.place.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdatePlaceRequest {

	@NotNull(message = "placeId는 비어 있을 수 없습니다.")
	private Long placeId;

	@NotBlank(message = "siDo는 비어 있을 수 없습니다.")
	private String siDo;

	@NotBlank(message = "siGunGu는 비어 있을 수 없습니다.")
	private String siGunGu;

	@NotBlank(message = "roadNameAddress는 비어 있을 수 없습니다.")
	private String roadNameAddress;

	@NotNull(message = "addressLat는 비어 있을 수 없습니다.")
	private Double addressLat;

	@NotNull(message = "addressLong는 비어 있을 수 없습니다.")
	private Double addressLong;
}
