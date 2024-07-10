package middle_point_search.backend.domains.market.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.domains.market.dto.response.KakaoSearchResponse.Document;

@Slf4j
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecommendPlacesFindResponse {

	private final String name;
	private final String siDo;
	private final String siGunGu;
	private final String roadNameAddress;
	private final Double addressLat;
	private final Double addressLong;
	private final String phoneNumber;
	private final String placeUrl;

	public static RecommendPlacesFindResponse from(Document document) {
		String[] splitAddress = document.getRoad_address_name().split(" ", 3);

		String siDo = null;
		String siGunGu = null;
		String roadNameAddress = null;
		if (splitAddress.length < 3) {
			log.warn("RecommendPlacesFindResponse : {}({})의 Index가 범위를 초과했습니다.", document.getPlace_name() ,document.getRoad_address_name());
		} else {
			siDo = splitAddress[0];
			siGunGu = splitAddress[1];
			roadNameAddress= splitAddress[2];
		}

		return new RecommendPlacesFindResponse(
			document.getPlace_name(),
			siDo,
			siGunGu,
			roadNameAddress,
			Double.parseDouble(document.getY()),
			Double.parseDouble(document.getY()),
			document.getPhone(),
			document.getPlace_url()
		);
	}
}
