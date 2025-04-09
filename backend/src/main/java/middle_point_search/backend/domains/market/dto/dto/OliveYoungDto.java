package middle_point_search.backend.domains.market.dto.dto;

import java.util.Arrays;

import middle_point_search.backend.domains.market.domain.Market;
import middle_point_search.backend.domains.market.domain.OliveYoung;

public record OliveYoungDto(
	String name,
	Double latitude,
	Double longitude,
	String address
) {

	public Market toMarketEntity() {
		String[] addressParts = parseAddress(address);

		return Market.builder()
			.name(parseOliveYoungName(name))
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

	public OliveYoung toOliveYoungEntity() {
		return OliveYoung.builder()
			.name(parseOliveYoungName(name))
			.latitude(latitude)
			.longitude(longitude)
			.address(address)
			.build();
	}

	private String parseOliveYoungName(String name) {
		// "올리브영 "으로 시작하고 "점"으로 끝나는 패턴에서 중간만 추출
		if (name.startsWith("올리브영") && name.endsWith("점")) {
			return name.replaceFirst("올리브영\\s*", "")  // 앞의 "올리브영"과 공백 제거
				.replaceFirst("점$", "");         // 뒤의 "점" 제거
		}
		return name; // 패턴이 맞지 않으면 원본 반환
	}

	public static OliveYoungDto from(OliveYoung oliveYoung) {
		return new OliveYoungDto(
			oliveYoung.getName(),
			oliveYoung.getLatitude(),
			oliveYoung.getLongitude(),
			oliveYoung.getAddress()
		);
	}
}
