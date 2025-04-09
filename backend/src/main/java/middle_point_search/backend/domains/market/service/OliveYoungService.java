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
import middle_point_search.backend.domains.market.domain.OliveYoung;
import middle_point_search.backend.domains.market.dto.dto.OliveYoungDto;
import middle_point_search.backend.domains.market.repository.OliveYoungRepository;
import middle_point_search.backend.domains.recommendPlace.dto.response.KakaoSearchResponse;

@Service
@RequiredArgsConstructor
public class OliveYoungService {

	private final KakaoProperties kakaoProperties;
	private final OliveYoungRepository oliveYoungRepository;
	private final WebClientUtil webClientUtil;


	// 모든 올리브영 위치 알아오기
	@Transactional
	public List<OliveYoungDto> getAllOliveYoung() {
		// 기존 데이터가 있으면 기존 데이터 리턴
		if (oliveYoungRepository.count() != 0) {
			return oliveYoungRepository.findAll()
				.stream()
				.map(OliveYoungDto::from)
				.toList();
		}

		// 한국 양 끝 좌표
		final double x1 = 124.60;
		final double y1 = 38.63;
		final double x2 = 131.87;
		final double y2 = 33.12;

		// 데이터를 조회하고 저장
		List<OliveYoungDto> oliveYoungDtos = searchRecursive(x1, y1, x2, y2);
		List<OliveYoung> oliveYoungs = oliveYoungDtos
			.stream()
			.map(OliveYoungDto::toOliveYoungEntity)
			.toList();
		oliveYoungRepository.saveAll(oliveYoungs);

		return oliveYoungDtos;
	}

	// 재귀를 통해 올리브영 위치 조회
	private List<OliveYoungDto> searchRecursive(double x1, double y1, double x2, double y2) {
		List<OliveYoungDto> results = new ArrayList<>();

		// 먼저 1차 조회
		KakaoSearchResponse response = searchOliveYoungMetaOnly(x1, y1, x2, y2);
		int totalCount = response.getMeta().getTotal_count();

		if (totalCount <= 45) {
			// 데이터가 많지 않으면 전체 페이지를 돌며 실제 데이터 수집
			results.addAll(searchOliveYoungFromKakao(x1, y1, x2, y2));
		} else {
			// 9등분 (3x3)
			double xStep = (x2 - x1) / 3.0;
			double yStep = (y1 - y2) / 3.0;

			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					double subX1 = x1 + xStep * i;
					double subY1 = y1 - yStep * j;
					double subX2 = x1 + xStep * (i + 1);
					double subY2 = y1 - yStep * (j + 1);


					results.addAll(searchRecursive(subX1, subY1, subX2, subY2));
				}
			}
		}

		return results;
	}

	// 올리브영 메타데이터 조회
	private KakaoSearchResponse searchOliveYoungMetaOnly(double x1, double y1, double x2, double y2) {
		MultiValueMap<String, String> params = makeParams(
			x1,
			y1,
			x2,
			y2,
			1,
			1
		);

		return webClientUtil.getKakao(
			kakaoProperties.getKeywordSearchUrl(),
			params,
			KakaoSearchResponse.class);
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
			kakaoSearchResponse.getDocuments()
				.forEach(document -> {
					OliveYoungDto oliveYoung = new OliveYoungDto(
						document.getPlace_name(),
						Double.parseDouble(document.getY()),
						Double.parseDouble(document.getX()),
						document.getRoad_address_name()
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
}
