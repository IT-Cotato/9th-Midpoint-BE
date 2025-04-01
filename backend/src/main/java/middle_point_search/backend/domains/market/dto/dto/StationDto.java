package middle_point_search.backend.domains.market.dto.dto;

import java.util.Objects;

import middle_point_search.backend.domains.market.domain.Market;

public record StationDto(
	String name,
	Double latitude,
	Double longitude,
	String address
) {

	public Market toMarketEntity() {
		return Market.builder()
			.name(name)
			.address(address)
			.addressLatitude(latitude)
			.addressLongitude(longitude)
			.build();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		StationDto that = (StationDto)o;
		return Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(name);
	}
}
