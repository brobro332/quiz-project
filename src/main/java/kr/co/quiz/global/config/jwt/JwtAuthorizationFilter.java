package kr.co.quiz.global.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.quiz.global.config.security.UserDetailsServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final UserDetailsServiceImpl userDetailsService;
    public JwtAuthorizationFilter(JwtProvider jwtProvider, UserDetailsServiceImpl userDetailsService) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
    }

    public UserDetails getUserDetailsByUsername(String username) {
        try {
            return userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            // 사용자를 찾을 수 없는 경우 처리

            throw new IllegalArgumentException("해당 사용자를 찾을 수 없습니다.");
        }
    }

    /**
     * doFilterInternal: 요청과 응답을 가로채서 사용자의 인증 및 권한 부여
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtProvider.resolveAccessToken(request);
        String refreshToken = jwtProvider.resolveRefreshToken(request);

        // accessToken이 유효한지 확인
        if (accessToken != null) {
            // 유효하다면
            if (jwtProvider.validateToken(accessToken)) {
                Authentication authentication = jwtProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            // accessToken이 만료되고 refreshToken이 존재한다면
            else if (!jwtProvider.validateToken(accessToken) && refreshToken != null) {
                // 재발급 후, Context에 다시 넣기
                boolean validateRefreshToken = jwtProvider.validateToken(refreshToken); // refreshToken 검증

                // 데이터베이스의 refreshToken과 비교
                UserDetails user = getUserDetailsByUsername(jwtProvider.parseClaims(refreshToken).getSubject());
                boolean compareRefreshToken = jwtProvider.compareRefreshToken(user.getUsername(), refreshToken);

                // refreshToken이 만료되지 않았고, 데이터베이스의 값과 동일하다면
                if (validateRefreshToken && compareRefreshToken) {

                    /// 새로운 accessToken 발급
                    String newAccessToken = jwtProvider.generateAccessToken(user);
                    // 새로운 accessToken 응답
                    response.addHeader("Authorization", "Bearer " + newAccessToken);
                    /// Context에 넣기
                    Authentication authentication = jwtProvider.getAuthentication(newAccessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
