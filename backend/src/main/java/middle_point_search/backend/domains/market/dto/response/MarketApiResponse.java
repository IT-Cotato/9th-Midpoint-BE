package middle_point_search.backend.domains.market.dto.response;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MarketApiResponse {
	private int page;
	private int perPage;
	private int totalCount;
	private int currentCount;
	private int matchCount;
	private List<MarketApiData> data;
}
