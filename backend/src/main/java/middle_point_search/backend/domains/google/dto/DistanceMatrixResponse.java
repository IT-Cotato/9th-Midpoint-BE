package middle_point_search.backend.domains.google.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DistanceMatrixResponse extends GoogleApiResponse {

	private List<String> destination_addresses;
	private List<String> origin_addresses;
	private List<Row> rows;

	@Getter
	@NoArgsConstructor
	public static class Row {
		private List<Element> elements;
	}

	@Getter
	@NoArgsConstructor
	public static class Element {
		private Distance distance;
		private Duration duration;
		private String status;
	}

	@Getter
	@NoArgsConstructor
	public static class Distance {
		private String text;
		private int value;
	}

	@Getter
	@NoArgsConstructor
	public static class Duration {
		private String text;
		private int value;
	}
}