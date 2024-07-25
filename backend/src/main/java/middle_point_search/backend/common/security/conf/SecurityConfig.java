package middle_point_search.backend.common.security.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import middle_point_search.backend.common.properties.SecurityProperties;
import middle_point_search.backend.common.security.exception.hadlingFilter.ExceptionHandlingFilter;
import middle_point_search.backend.common.security.handler.CustomAccessDeniedHandler;
import middle_point_search.backend.common.security.handler.CustomAuthenticationEntryPoint;
import middle_point_search.backend.common.security.jwt.filter.JwtAuthenticationFilter;
import middle_point_search.backend.common.security.jwt.provider.JwtTokenProvider;
import middle_point_search.backend.common.security.login.filter.JsonNamePwAuthenticationFilter;
import middle_point_search.backend.common.security.login.handler.LoginFailureHandler;
import middle_point_search.backend.common.security.login.handler.LoginSuccessHandler;
import middle_point_search.backend.common.security.login.provider.CustomAuthenticationProvider;
import middle_point_search.backend.domains.member.repository.MemberRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final ObjectMapper objectMapper;
	private final CustomAuthenticationProvider authenticationProvider;
	private final JwtTokenProvider jwtTokenProvider;
	private final MemberRepository memberRepository;

	private final SecurityProperties securityProperties;
	private final UrlBasedCorsConfigurationSource ConfigurationSource;

	private final LoginSuccessHandler loginSuccessHandler;
	private final LoginFailureHandler loginFailureHandler;

	private final AntPathMatcher pathMatcher = new AntPathMatcher();

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable) // 세션을 사용안하므로 csrf 공격 없으므로 csrf 비활성화
			.httpBasic(AbstractHttpConfigurer::disable) // 기본 인증 방식 비활성화
			.cors(cors -> cors.configurationSource(ConfigurationSource))
			.formLogin(AbstractHttpConfigurer::disable) //json을 이용하여 로그인을 하므로 기본 Login 비활성화
			.authorizeHttpRequests((authorize) -> authorize // PERMIT_URLS만 바로 접근 가능, 나머지 URL은 인증 필요
				.requestMatchers(securityProperties.getPermitUrls()).permitAll()
				.requestMatchers(securityProperties.getAuthorizationRequiredUrls()).hasAnyRole("USER", "ADMIN")
				.requestMatchers(securityProperties.getAdminUrls()).hasRole("ADMIN")
				.anyRequest().authenticated()
			)
			.logout(LogoutConfigurer::disable) // 로그아웃 비활성화
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 토큰을 사용하기 때문에 Session 사용 x
		;

		//필터 체인 추가
		http
			.addFilterAfter(jsonUsernamePasswordLoginFilter(), LogoutFilter.class)
			.addFilterBefore(jwtAuthenticationFilter(), JsonNamePwAuthenticationFilter.class)
			.addFilterBefore(exceptionHandlingFilter(), JwtAuthenticationFilter.class);

		//예외 처리 추가
		http
			.exceptionHandling(
				httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(
					new CustomAuthenticationEntryPoint())
			)
			.exceptionHandling(
				httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(
					new CustomAccessDeniedHandler())
			);

		return http.build();
	}

	//authentication Provider 관리를 위한 Manager 등록
	@Bean
	public AuthenticationManager authenticationManager() throws Exception {
		return new ProviderManager(authenticationProvider);
	}

	//로그인 필터 등록
	@Bean
	public JsonNamePwAuthenticationFilter jsonUsernamePasswordLoginFilter() throws Exception {
		JsonNamePwAuthenticationFilter jsonUsernamePasswordLoginFilter = new JsonNamePwAuthenticationFilter(
			objectMapper);
		jsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());

		jsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessHandler);
		jsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler);

		return jsonUsernamePasswordLoginFilter;
	}

	//JWT 필터 등록
	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(jwtTokenProvider, memberRepository, securityProperties, pathMatcher);
	}

	//예외 핸들링 필터 등록
	@Bean
	public ExceptionHandlingFilter exceptionHandlingFilter() {
		return new ExceptionHandlingFilter();
	}

}
