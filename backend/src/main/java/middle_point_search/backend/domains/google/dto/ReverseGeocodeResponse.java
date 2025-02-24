package middle_point_search.backend.domains.google.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReverseGeocodeResponse extends GoogleApiResponse {
	private PlusCode plus_code;
	private List<Result> results;

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class PlusCode {
		private String compound_code;
		private String global_code;
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Result {
		private List<AddressComponent> address_components;
		private String formatted_address;
		private Geometry geometry;
		private String place_id;
		private List<String> types;

		@Getter
		@NoArgsConstructor(access = AccessLevel.PRIVATE)
		public static class AddressComponent {
			private String long_name;
			private String short_name;
			private List<String> types;
		}

		@Getter
		@NoArgsConstructor(access = AccessLevel.PRIVATE)
		public static class Geometry {
			private Location location;
			private String location_type;
			private Viewport viewport;

			@Getter
			@NoArgsConstructor(access = AccessLevel.PRIVATE)
			public static class Location {
				private Double lat;
				private Double lng;
			}

			@Getter
			@NoArgsConstructor(access = AccessLevel.PRIVATE)
			public static class Viewport {
				private Location northeast;
				private Location southwest;
			}
		}
	}
}