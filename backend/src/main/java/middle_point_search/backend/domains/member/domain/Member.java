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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import middle_point_search.backend.domains.room.domain.Room;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Table(uniqueConstraints = {@UniqueConstraint(name = "NAME_ROOM_UNIQUE", columnNames = {"NAME", "ROOM"})})
public class Member {

	@Id
	@Column(name = "MEMBER_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NonNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ROOM_ID")
	private Room room;

	@NonNull
	@Column(nullable = false)
	private String name;

	@NonNull
	@Column(nullable = false)
	private String pw;

	@NonNull
	@Enumerated(EnumType.STRING)
	private Transport transport;

	@NonNull
	@Column(nullable = false)
	private String siDo;

	@NonNull
	@Column(nullable = false)
	private String siGunGu;

	@NonNull
	@Column(nullable = false)
	private String roadNameAddress;

	@NonNull
	@Column(nullable = false)
	private Double addressLatitude;

	@NonNull
	@Column(nullable = false)
	private Double addressLongitude;

	public Member from(Room room, String name, String pw, Transport transport, String siDo, String siGunGu,
		String roadNameAddress, Double addressLatitude, Double addressLongitude) {
		addRoom(room);

		return new Member(room, name, pw, transport, siDo, siGunGu, roadNameAddress, addressLatitude, addressLongitude);
	}

	public void addRoom(Room room) {
		this.room = room;
		room.getMembers().add(this);
	}
}
