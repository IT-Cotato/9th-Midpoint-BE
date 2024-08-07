package middle_point_search.backend.domains.room.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import middle_point_search.backend.domains.member.domain.Member;
import middle_point_search.backend.domains.place.domain.Place;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Room {

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@Id
	@Column(name = "room_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String identityNumber;

	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Member> members = new ArrayList<>();

	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Place> places = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	private RoomType roomType;

	@Enumerated(EnumType.STRING)
	private Animal roomName;

	public Room(String identityNumber, RoomType roomType) {
		this.identityNumber = identityNumber;
		this.roomType = roomType;
		this.roomName = Animal.getRandomAnimal();
	}

	public static Room from(String identityNumber, RoomType roomType) {
		return new Room(identityNumber, roomType);
	}

}
