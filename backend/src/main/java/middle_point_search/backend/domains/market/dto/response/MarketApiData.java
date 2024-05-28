package middle_point_search.backend.domains.market.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MarketApiData {
	private String 데이터기준일자;
	private String 상권명;
	private int 상권번호;
	private String 상권좌표;
	private int 상권좌표수;
	private String 시군구명;
	private int 시군구코드;
	private String 시도명;
	private int 시도코드;

	public String getName() {
		return this.상권명;
	}

	public String getCoordinates() {
		return this.상권좌표;
	}

	public String getSiGunGu() {
		return this.시군구명;
	}

	public String getSiDo() {
		return this.시도명;
	}

	@Override
	public String toString() {
		return "MarketApiData{" +
			"데이터기준일자='" + 데이터기준일자 + '\'' +
			", 상권명='" + 상권명 + '\'' +
			", 상권번호=" + 상권번호 +
			", 상권좌표='" + 상권좌표 + '\'' +
			", 상권좌표수=" + 상권좌표수 +
			", 시군구명='" + 시군구명 + '\'' +
			", 시군구코드=" + 시군구코드 +
			", 시도명='" + 시도명 + '\'' +
			", 시도코드=" + 시도코드 +
			'}';
	}
}
