package middle_point_search.backend.domains.member.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import middle_point_search.backend.domains.member.domain.Member;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FindMemberInfoResponse(
	String email,
	String name,
	String existAddress,
	String siDo,
	String siGunGu,
	String roadNameAddress,
	Double addressLatitude,
	Double addressLongitude,
	Boolean isOauth
) {
	public static FindMemberInfoResponse from(Member member) {
		if (member.getExistAddress()) {
			return new FindMemberInfoResponse(
				member.getEmail(),
				member.getName(),
				"true",
				member.getSiDo(),
				member.getSiGunGu(),
				member.getRoadNameAddress(),
				member.getAddressLatitude(),
				member.getAddressLongitude(),
				member.getProvider() != null
			);
		} else {
			return new FindMemberInfoResponse(
				member.getEmail(),
				member.getName(),
				"false",
				null,
				null,
				null,
				null,
				null,
				member.getProvider() != null
			);
		}
	}
}
