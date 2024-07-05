package middle_point_search.backend.common.security.dto;

import org.springframework.security.core.userdetails.UserDetails;

public interface CustomUserDetails extends UserDetails {
	String getRoomId();
	//수정
	Long getId();
}
