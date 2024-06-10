package middle_point_search.backend.common.security.login.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface CustomUserDetailsService extends UserDetailsService {

	UserDetails loadUserByUsernameAndRoomId(String username, String roomId) throws UsernameNotFoundException;
}
