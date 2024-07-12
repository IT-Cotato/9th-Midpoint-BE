package middle_point_search.backend.domains.member.domain;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import middle_point_search.backend.domains.place.domain.Place;
import middle_point_search.backend.domains.room.domain.Room;

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
	private Room room;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String pw;

	@Setter
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "PLACE_ID")
	private Place place;

	@Column(length = 3000)
	private String refreshToken;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	private Member(Room room, String name, String pw, Role role) {
		addRoom(room);
		this.room = room;
		this.name = name;
		this.pw = pw;
		this.role = role;
	}

	public static Member from(Room room, String name, String pw, Role role) {


		return new Member(room, name, pw, role);
	}

	private void addRoom(Room room) {
		this.room = room;
		room.getMembers().add(this);
	}

	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void destroyRefreshToken() {
		this.refreshToken = null;
	}

	public void deletePlace() {
		this.place = null;
	}

	public void updateRole(Role role) {
		this.role = role;
	}
}
