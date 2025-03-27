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
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import middle_point_search.backend.common.baseEntity.BaseEntity;
import middle_point_search.backend.domains.memberRoom.domain.MemberRoom;
import middle_point_search.backend.domains.place.domain.Place;
import middle_point_search.backend.domains.placeVoteRoom.domain.PlaceVoteRoom;
import middle_point_search.backend.domains.timeVoteRoom.domain.TimeVoteRoom;

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

	@Column(name = "room_memo")
	private String memo;

	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<MemberRoom> memberRooms = new ArrayList<>();

	@OneToOne(mappedBy = "room", cascade = CascadeType.ALL)
	private TimeVoteRoom timeVoteRoom;

	@OneToOne(mappedBy = "room", cascade = CascadeType.ALL)
	private PlaceVoteRoom placeVoteRoom;

	@Builder
	private Room(String id, String name, String memo) {
		this.name = name;
		this.memo = memo;
		this.id = id;
	}

	public void updateName(String name) {
		this.name = name;
	}

	public void updateMemo(String memo) {
		this.memo = memo;
	}
}
