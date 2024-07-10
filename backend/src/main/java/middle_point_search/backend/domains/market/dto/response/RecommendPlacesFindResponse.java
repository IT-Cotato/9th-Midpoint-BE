package middle_point_search.backend.domains.market.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.domains.market.domain.PlaceStandard;
import middle_point_search.backend.domains.market.dto.response.KakaoSearchResponse.Document;

@Slf4j
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecommendPlacesFindResponse implements Comparable<RecommendPlacesFindResponse> {

	private final String name;
	private final String siDo;
	private final String siGunGu;
	private final String roadNameAddress;
	private final Double addressLat;
	private final Double addressLong;
	private final String phoneNumber;
	private final String placeUrl;
	private final PlaceStandard placeStandard;
	private final String distance;

	public static RecommendPlacesFindResponse from(Document document, PlaceStandard placeStandard) {

		String[] splitAddress = splitAddress(document.getRoad_address_name());

		String siDo = splitAddress[0];
		String siGunGu = splitAddress[1];
		String roadNameAddress = splitAddress[2];

		return new RecommendPlacesFindResponse(
			document.getPlace_name(),
			siDo,
			siGunGu,
			roadNameAddress,
			Double.parseDouble(document.getY()),
			Double.parseDouble(document.getY()),
			document.getPhone(),
			document.getPlace_url(),
			placeStandard,
			document.getDistance()
		);
	}

	//도로명 주소를 시, 구, 나머지로 쪼개는 메서드
	private static String[] splitAddress(String address) {
		String[] splitAddress = address.split(" ", 3);
		String[] result = new String[3];

		for (int i = 0; i < splitAddress.length; i++) {
			result[i] = splitAddress[i];
		}

		return result;
	}

	@Override
	public int compareTo(RecommendPlacesFindResponse o) {
		return Integer.parseInt(this.distance) - Integer.parseInt(o.distance);
	}
}
