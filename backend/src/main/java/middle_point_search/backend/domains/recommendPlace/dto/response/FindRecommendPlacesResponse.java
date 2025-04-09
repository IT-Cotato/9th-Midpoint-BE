package middle_point_search.backend.domains.recommendPlace.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import middle_point_search.backend.domains.market.domain.PlaceStandard;
import middle_point_search.backend.domains.recommendPlace.dto.response.KakaoSearchResponse.Document;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FindRecommendPlacesResponse implements Comparable<FindRecommendPlacesResponse> {

	private String name;
	private String siDo;
	private String siGunGu;
	private String roadNameAddress;
	private Double addressLat;
	private Double addressLong;
	private String phoneNumber;
	private String placeUrl;
	private PlaceStandard placeStandard;
	private String distance;

	public static FindRecommendPlacesResponse from(Document document, PlaceStandard placeStandard) {

		String[] splitAddress = splitAddress(document.getRoad_address_name());

		String siDo = splitAddress[0];
		String siGunGu = splitAddress[1];
		String roadNameAddress = splitAddress[2];

		return new FindRecommendPlacesResponse(
			document.getPlace_name(),
			siDo,
			siGunGu,
			roadNameAddress,
			Double.parseDouble(document.getY()),
			Double.parseDouble(document.getX()),
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
	public int compareTo(FindRecommendPlacesResponse o) {
		return Integer.parseInt(this.distance) - Integer.parseInt(o.distance);
	}
}
