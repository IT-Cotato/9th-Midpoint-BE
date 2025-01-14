package middle_point_search.backend.domains.room.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import middle_point_search.backend.common.baseEntity.BaseEntity;
import middle_point_search.backend.domains.memberRoom.MemberRoom;
import middle_point_search.backend.domains.place.domain.Place;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Room extends BaseEntity {

	@Id
	@Column(name = "room_id", nullable = false, length = 36)
	private String id;

	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Place> places = new ArrayList<>();

	@Column(name = "room_name", nullable = false)
	private String name;

	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<MemberRoom> memberRooms = new ArrayList<>();

	@Builder
	private Room(String name, String id) {
		this.name = name;
		this.id = id;
	}

	public void updateName(String name) {
		this.name = name;
	}
}
