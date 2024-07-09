package middle_point_search.backend.domains.place.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.member.domain.Transport;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlaceSaveRequest;
import middle_point_search.backend.domains.place.dto.PlaceDTO.PlacesUpdateRequest.PlaceUpdateRequest;
import middle_point_search.backend.domains.room.domain.Room;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

	@Id
	@Column(name = "PLACE_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private Transport transport;

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
	@JoinColumn(name = "ROOM_ID")
	private Room room;

	@OneToOne(mappedBy = "place", fetch = FetchType.LAZY)
	private Member member;

	private Place(Transport transport, String siDo, String siGunGu, String roadNameAddress, Double addressLatitude,
		Double addressLongitude, Room room) {
		this.transport = transport;
		this.siDo = siDo;
		this.siGunGu = siGunGu;
		this.roadNameAddress = roadNameAddress;
		this.addressLatitude = addressLatitude;
		this.addressLongitude = addressLongitude;
		addRoom(room);
	}

	private Place(Transport transport, String siDo, String siGunGu, String roadNameAddress, Double addressLatitude,
		Double addressLongitude, Room room, Member member) {
		this.transport = transport;
		this.siDo = siDo;
		this.siGunGu = siGunGu;
		this.roadNameAddress = roadNameAddress;
		this.addressLatitude = addressLatitude;
		this.addressLongitude = addressLongitude;
		addRoom(room);
		addMember(member);
	}

	public static Place of(Transport transport, String siDo, String siGunGu, String roadNameAddress,
		Double addressLatitude, Double addressLongitude, Room room) {
		return new Place(transport, siDo, siGunGu, roadNameAddress, addressLatitude, addressLongitude, room);
	}

	public static Place of(Transport transport, String siDo, String siGunGu, String roadNameAddress,
		Double addressLatitude, Double addressLongitude, Room room, Member member) {
		return new Place(transport, siDo, siGunGu, roadNameAddress, addressLatitude, addressLongitude, room, member);
	}

	public static Place from(PlaceSaveRequest placeSaveRequest, Room room, Member member) {
		Transport transport = placeSaveRequest.getTransport();
		String siDo = placeSaveRequest.getSiDo();
		String siGunGu = placeSaveRequest.getSiGunGu();
		String roadNameAddress = placeSaveRequest.getRoadNameAddress();
		Double addressLatitude = placeSaveRequest.getAddressLat();
		Double addressLongitude = placeSaveRequest.getAddressLong();

		return new Place(transport, siDo, siGunGu, roadNameAddress, addressLatitude, addressLongitude, room, member);
	}

	public static Place from(PlaceSaveRequest placeSaveRequest, Room room) {
		Transport transport = placeSaveRequest.getTransport();
		String siDo = placeSaveRequest.getSiDo();
		String siGunGu = placeSaveRequest.getSiGunGu();
		String roadNameAddress = placeSaveRequest.getRoadNameAddress();
		Double addressLatitude = placeSaveRequest.getAddressLat();
		Double addressLongitude = placeSaveRequest.getAddressLong();

		return new Place(transport, siDo, siGunGu, roadNameAddress, addressLatitude, addressLongitude, room);
	}

	private void addRoom(Room room) {
		this.room = room;
		room.getPlaces().add(this);
	}

	private void addMember(Member member) {
		this.member = member;
		member.setPlace(this);
	}

	public void update(PlaceUpdateRequest request) {
		this.transport = request.getTransport();
		this.siDo = request.getSiDo();
		this.siGunGu = request.getSiGunGu();
		this.roadNameAddress = request.getRoadNameAddress();
		this.addressLatitude = request.getAddressLat();
		this.addressLongitude = request.getAddressLong();
	}
}
