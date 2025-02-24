package middle_point_search.backend.domains.member.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateMemberAddressRequest(
	@NotBlank(message = "siDo는 비어있을 수 없습니다.") String siDo,
	@NotBlank(message = "siGunGu는 비어있을 수 없습니다.") String siGunGu,
	@NotBlank(message = "roadNameAddress는 비어있을 수 없습니다.") String roadNameAddress,
	Double addressLatitude,
	Double addressLongitude
) {
}
