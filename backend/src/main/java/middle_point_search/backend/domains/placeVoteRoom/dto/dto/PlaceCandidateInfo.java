package middle_point_search.backend.domains.placeVoteRoom.dto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlaceCandidateInfo {

	@NotBlank(message = "name은 비어 있을 수 없습니다.")
	private String name;
	@NotBlank(message = "siDo는 비어 있을 수 없습니다.")
	private String siDo;
	@NotBlank(message = "siGunGu는 비어 있을 수 없습니다.")
	private String siGunGu;
	@NotBlank(message = "roadNameAddress는 비어 있을 수 없습니다.")
	private String roadNameAddress;
	@NotNull
	@Positive(message = "addreesLat은 양수이어야 합니다.")
	private Double addressLat;
	@NotNull
	@Positive(message = "addreesLong은 양수이어야 합니다.")
	private Double addressLong;
}
