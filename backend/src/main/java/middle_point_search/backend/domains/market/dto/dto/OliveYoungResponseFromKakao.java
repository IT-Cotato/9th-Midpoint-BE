package middle_point_search.backend.domains.market.dto.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import middle_point_search.backend.domains.recommendPlace.dto.response.KakaoSearchResponse;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OliveYoungResponseFromKakao {
	List<OliveYoungDto> oliveYoungs;
	int totalCount;

	public static OliveYoungResponseFromKakao from(KakaoSearchResponse kakaoSearchResponse) {
		return new OliveYoungResponseFromKakao(
			kakaoSearchResponse.getDocuments()
				.stream()
				.map(document -> new OliveYoungDto(
					document.getPlace_name(),
					Double.parseDouble(document.getY()),
					Double.parseDouble(document.getX()),
					document.getAddress_name()
				))
				.toList(),
			kakaoSearchResponse.getMeta().getTotal_count()
		);
	}
}
