package middle_point_search.backend.domains.market.dto.dto;

import java.util.Arrays;
import java.util.Objects;

import middle_point_search.backend.domains.market.domain.Market;

public record StationDto(
	String name,
	Double latitude,
	Double longitude,
	String address
) {

	public Market toMarketEntity() {
		String[] addressParts = parseAddress(address);

		return Market.builder()
			.name(name)
			.siDo(addressParts[0])
			.siGunGu(addressParts[1])
			.roadNameAddress(addressParts[2])
			.addressLatitude(latitude)
			.addressLongitude(longitude)
			.build();
	}

	private String[] parseAddress(String address) {
		if (address == null || address.isBlank()) {
			return new String[] {"", "", ""};
		}

		String[] addressParts = address.split(" ");
		if (addressParts.length < 3) {
			return new String[] {"", "", ""};
		}

		String siDo = addressParts[0];
		String siGunGu = addressParts[1];
		String roadNameAddress = String.join(" ", Arrays.copyOfRange(addressParts, 2, addressParts.length));

		return new String[] {siDo, siGunGu, roadNameAddress};
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
