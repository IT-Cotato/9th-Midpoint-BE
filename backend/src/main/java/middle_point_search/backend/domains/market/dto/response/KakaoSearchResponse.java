package middle_point_search.backend.domains.market.dto.response;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KakaoSearchResponse {
	private Meta meta;
	private List<Document> documents;

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Meta {
		private SameName same_name;
		private int pageable_count;
		private int total_count;
		private boolean is_end;

		@Getter
		@NoArgsConstructor(access = AccessLevel.PRIVATE)
		public static class SameName {
			private List<String> region;
			private String keyword;
			private String selected_region;
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Document implements Comparable<Document>{
		private String place_name;
		private String distance;
		private String place_url;
		private String category_name;
		private String address_name;
		private String road_address_name;
		private String id;
		private String phone;
		private String category_group_code;
		private String category_group_name;
		private String x;
		private String y;

		@Override
		public int compareTo(Document o) {
			return Integer.parseInt(this.distance) - Integer.parseInt(o.distance);
		}
	}
}
