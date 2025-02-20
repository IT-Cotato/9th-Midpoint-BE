package middle_point_search.backend.domains.place.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.place.dto.request.SavePlaceRequest;
import middle_point_search.backend.domains.room.domain.Room;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

	@Id
	@Column(name = "place_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String siDo;

	@Column(nullable = false)
	private String siGunGu;

	@Column(nullable = false)
	private String roadNameAddress;

	@Column(nullable = false)
	private Double addressLatitude;

	@Column(nullable = false)
	private Double addressLongitude;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room_id")
	private Room room;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Column(nullable = false)
	private String googlePlaceId;

	@Builder
	private Place(String siDo, String siGunGu, String roadNameAddress, Double addressLatitude,
		Double addressLongitude, Room room, Member member, String googlePlaceId) {
		this.siDo = siDo;
		this.siGunGu = siGunGu;
		this.roadNameAddress = roadNameAddress;
		this.addressLatitude = addressLatitude;
		this.addressLongitude = addressLongitude;
		addRoom(room);
		this.member = member;
		this.googlePlaceId = googlePlaceId;
	}

	public static Place
	from(
		SavePlaceRequest request,
		Room room,
		Member member,
		String googlePlaceId
	) {
		return new Place(
			request.getSiDo(),
			request.getSiGunGu(),
			request.getRoadNameAddress(),
			request.getAddressLat(),
			request.getAddressLong(),
			room,
			member,
			googlePlaceId
		);
	}

	private void addRoom(Room room) {
		this.room = room;
		room.getPlaces().add(this);
	}
}
