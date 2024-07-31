package middle_point_search.backend.domains.placeVoteRoom.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static middle_point_search.backend.domains.placeVoteRoom.dto.PlaceVoteRoomDTO.PlaceVoteRoomCreateRequest.PlaceCandidateInfo;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceVoteCandidate {

	@Id
	@Column(name = "place_vote_candidate_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

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
	@JoinColumn(name = "place_vote_room_id")
	private PlaceVoteRoom placeVoteRoom;

	@OneToMany(mappedBy = "placeVoteCandidate", cascade = {CascadeType.PERSIST,
		CascadeType.MERGE}, orphanRemoval = true)
	private List<PlaceVoteCandidateMember> voters = new ArrayList<>();

	public PlaceVoteCandidate(PlaceCandidateInfo candidate, PlaceVoteRoom placeVoteRoom) {
		this.name = candidate.getName();
		this.siDo = candidate.getSiDo();
		this.siGunGu = candidate.getSiGunGu();
		this.roadNameAddress = candidate.getRoadNameAddress();
		this.addressLatitude = candidate.getAddressLat();
		this.addressLongitude = candidate.getAddressLong();
		this.placeVoteRoom = placeVoteRoom;
	}

	public int getCount() {
		return voters.size();
	}
}

