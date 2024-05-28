package middle_point_search.backend.domains.Room.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room {

	@Id
	@Column(name = "ROOM_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String identityNumber;

	private LocalDateTime createDate;

	public Room(String identityNumber) {
		this.identityNumber = identityNumber;
		this.createDate = LocalDateTime.now();
	}

	public static Room from(String identityNumber) {
		return new Room(identityNumber);
	}
}
