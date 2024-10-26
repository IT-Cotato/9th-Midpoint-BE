package middle_point_search.backend.domains.placeVoteRoom.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import middle_point_search.backend.domains.room.domain.Room;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceVoteRoom {

	@Id
	@Column(name = "place_vote_room_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "room_id")
	private Room room;

	@OneToMany(mappedBy = "placeVoteRoom", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<PlaceVoteCandidate> placeVoteCandidates = new ArrayList<>();

	public PlaceVoteRoom(Room room) {
		this.room = room;
	}

	// 장소 투표 후보 추가
	public void addPlaceVoteCandidate(PlaceVoteCandidate placeVoteCandidate) {
		this.placeVoteCandidates.add(placeVoteCandidate);
	}

	// 장소 투표방 리셋
	public void resetPlaceVoteRoom() {
		this.placeVoteCandidates.clear();
	}
}

