package middle_point_search.backend.domains.member.domain;

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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import middle_point_search.backend.domains.room.domain.GroupRoom;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {@UniqueConstraint(name = "NAME_ROOM_UNIQUE", columnNames = {"NAME", "ROOM"})})
public class Member {

	@Id
	@Column(name = "MEMBER_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ROOM_ID")
	private GroupRoom room;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String pw;

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

	private Member(GroupRoom room, String name, String pw, Transport transport,
		String siDo, String siGunGu, String roadNameAddress, Double addressLatitude,
		Double addressLongitude) {
		addRoom(room);
		this.name = name;
		this.pw = pw;
		this.transport = transport;
		this.siDo = siDo;
		this.siGunGu = siGunGu;
		this.roadNameAddress = roadNameAddress;
		this.addressLatitude = addressLatitude;
		this.addressLongitude = addressLongitude;
	}

	public Member from(GroupRoom room, String name, String pw, Transport transport, String siDo, String siGunGu,
		String roadNameAddress, Double addressLatitude, Double addressLongitude) {
		addRoom(room);

		return new Member(room, name, pw, transport, siDo, siGunGu, roadNameAddress, addressLatitude, addressLongitude);
	}

	public void addRoom(GroupRoom room) {
		this.room = room;
		room.getMembers().add(this);
	}
}
