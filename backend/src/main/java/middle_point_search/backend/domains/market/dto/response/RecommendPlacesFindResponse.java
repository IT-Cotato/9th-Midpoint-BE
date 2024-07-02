package middle_point_search.backend.domains.market.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import middle_point_search.backend.domains.market.dto.response.KakaoSearchResponse.Document;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecommendPlacesFindResponse {

	private final String name;
	private final String siDo;
	private final String siGunGu;
	private final String roadNameAddress;
	private final Double addressLat;
	private final Double addressLong;

	public static RecommendPlacesFindResponse from(Document document) {
		String[] splitAddress = document.getRoad_address_name().split(" ", 3);
		String siDo = splitAddress[0];
		String siGunGu = splitAddress[1];
		String roadNameAddress= splitAddress[2];

		return new RecommendPlacesFindResponse(
			document.getPlace_name(),
			siDo,
			siGunGu,
			roadNameAddress,
			Double.parseDouble(document.getY()),
			Double.parseDouble(document.getY())
		);
	}
}
