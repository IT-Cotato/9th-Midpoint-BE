package middle_point_search.backend.domains.recommendPlace.dto;

import lombok.AllArgsConstructor;


public record KakaoRequestDTO (String x, String y, int page, int size) {
}
