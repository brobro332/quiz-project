package kr.co.quiz.global.config.security;

import kr.co.quiz.global.config.jwt.JwtAuthorizationFilter;
import kr.co.quiz.global.config.jwt.JwtProvider;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtProvider jwtProvider;
    private final UserDetailsServiceImpl userDetailsService;
    public SecurityConfig(JwtProvider jwtProvider, UserDetailsServiceImpl userDetailsService) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
    }

    /**
     * WebSecurityCustomizer: 정적 자원과 H2-Console에 대해 스프링 시큐리티를 적용하지 않음
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {

        return web -> web
                .ignoring()
                .requestMatchers(PathRequest.toH2Console())
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    /**
     * BCryptPasswordEncoder: 패스워드 암호화
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    /**
     * SecurityFilterChain: 스프링 시큐리티의 필터 정의 및 구성
     * 1. csrf 보안 비활성화
     * 2. STATELESS 설정
     * 3. Form 기반 로그인 비활성화
     * 4. HTTP 기본 인증 비활성화
     * 5. Endpoint 접근 거부 및 허용
     * 6. JWT 필터 설정
     * 7. H2-Console에 접속하기 위한 설정
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // csrf 설정: 서버에 인증정보를 저장하지 않으므로 csrf 공격에 취약하지 않음
                .csrf(AbstractHttpConfigurer::disable)

                // STATELESS 설정: 세선쿠키 기반의 인증처리를 하지않음
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Form 기반 로그인 비활성화: 커스텀으로 구성한 필터를 사용해야함
                .formLogin(AbstractHttpConfigurer::disable)

                // HTTP 기본 인증을 비활성화: HTTP 요청 헤더에 사용자 ID와 PW를 인코딩 및 포함시키지 않음
                .httpBasic(AbstractHttpConfigurer::disable)

                // 접근 거부 및 허용 처리
                .authorizeHttpRequests((authorizeRequest) -> authorizeRequest
                        .requestMatchers(AntPathRequestMatcher
                                .antMatcher("/api/v1/user")).permitAll() // 회원가입
                        .requestMatchers(AntPathRequestMatcher
                                .antMatcher("/api/v1/user/login")).permitAll() // 로그인
                        .anyRequest().authenticated()
                )

                // JWT 필터 추가
                .addFilterBefore(new JwtAuthorizationFilter(jwtProvider, userDetailsService), UsernamePasswordAuthenticationFilter.class)

                // H2-Console에 접속하기 위한 설정
                .headers(authorize -> authorize.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        return http.build();
    }
}
