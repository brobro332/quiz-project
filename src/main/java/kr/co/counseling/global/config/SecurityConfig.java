package kr.co.counseling.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. JWT 필터 추가
    
    
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http,
                                              JwtProvider jwtProvider) throws Exception {
        http
                // 2. CORS 설정: 리액트와의 통신을 위함
                .cors(AbstractHttpConfigurer::disable)

                // 3. STATELESS 설정: 세선쿠키 인증처리를 하지않기 위함
                .sessionManagement((sessionManagement) ->
                    sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 4. 접근 거부/허용 처리
                .authorizeHttpRequests((authorizeRequest) ->
                    authorizeRequest.anyRequest().permitAll()
                );

        return http.build();
    }
}
