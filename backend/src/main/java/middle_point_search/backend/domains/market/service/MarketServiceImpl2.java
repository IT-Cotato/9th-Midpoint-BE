package middle_point_search.backend.domains.market.service;

import static java.nio.charset.StandardCharsets.*;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.properties.KakaoProperties;
import middle_point_search.backend.common.webClient.util.WebClientUtil;
import middle_point_search.backend.domains.market.dto.dto.OliveYoungDto;
import middle_point_search.backend.domains.market.repository.OliveYoungRepository;
import middle_point_search.backend.domains.recommendPlace.dto.response.KakaoSearchResponse;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MarketServiceImpl2 implements MarketService {

	private final WebClientUtil webClientUtil;
	private final KakaoProperties kakaoProperties;
	private final OliveYoungRepository oliveYoungRepository;

	@Override
	@Transactional
	public void updateMarket() {
	}

	// 모든 올리브영 위치 알아오기
	private List<OliveYoungDto> getAllOliveYoung() {
		// 한국 양 끝 좌표
		final double x1 = 124.60;
		final double y1 = 38.63;
		final double x2 = 131.87;
		final double y2 = 33.12;

		int numberOfDivisions = 10; // 한국을 NxN으로 나누어서 검색
		double xDivisionSize = (x2 - x1) / (double)numberOfDivisions;
		double yDivisionSize = (y1 - y2) / (double)numberOfDivisions;

		List<OliveYoungDto> oliveYoungs = new ArrayList<>();
		for (int xIndex = 0; xIndex < numberOfDivisions - 1; xIndex++) {
			for (int yIndex = 0; yIndex < numberOfDivisions - 1; yIndex++) {
				double tmpX1 = x1 + xDivisionSize * (double)xIndex;
				double tmpY1 = y1 + yDivisionSize * (double)yIndex;
				double tmpX2 = x1 + xDivisionSize * (double)(xIndex + 1);
				double tmpY2 = y1 - yDivisionSize * (double)(yIndex + 1);

				oliveYoungs.addAll(searchOliveYoungFromKakao(tmpX1, tmpY1, tmpX2, tmpY2));
			}
		}

		return oliveYoungs;
	}

	// 카카오에서 올리브영 위치 검색
	private List<OliveYoungDto> searchOliveYoungFromKakao(double x1, double y1, double x2, double y2) {
		int nextPage = 1;
		int size = 15;

		List<OliveYoungDto> oliveYoungs = new ArrayList<>();

		boolean hasNext = true;
		while (hasNext) {
			MultiValueMap<String, String> params = makeParams(
				x1,
				y1,
				x2,
				y2,
				size,
				nextPage++
			);

			KakaoSearchResponse kakaoSearchResponse = webClientUtil.getKakao(
				kakaoProperties.getKeywordSearchUrl(),
				params,
				KakaoSearchResponse.class);

			// 다음 페이지가 있는지 확인
			hasNext = Optional.ofNullable(kakaoSearchResponse.getMeta().getIs_end())
				.map(isEnd -> !isEnd)
				.orElse(false);

			// 올리브영만 추출
			kakaoSearchResponse.getDocuments().stream()
				.filter(document -> document.getCategory_name().equals("올리브영"))
				.forEach(document -> {
					OliveYoungDto oliveYoung = new OliveYoungDto(
						document.getPlace_name(),
						Double.parseDouble(document.getX()),
						Double.parseDouble(document.getY())
					);
					oliveYoungs.add(oliveYoung);
				});
		}
		return oliveYoungs;
	}

	//기본적인 param을 만들어주는 메서드
	private MultiValueMap<String, String> makeParams(
		double x1,
		double y1,
		double x2,
		double y2,
		int size,
		int page
	) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("rect",
			String.format("%.6f", x1) + "," + String.format("%.6f", y1) + "," + String.format("%.6f", x2) + ","
				+ String.format("%.6f", y2));
		params.add(kakaoProperties.getParamSize(), String.valueOf(size));
		params.add(kakaoProperties.getParamPage(), String.valueOf(page));
		params.add(kakaoProperties.getParamQuery(), URLEncoder.encode("올리브영", UTF_8));
		return params;
	}

	// 모든 지하철역 위치 알아오기

}
