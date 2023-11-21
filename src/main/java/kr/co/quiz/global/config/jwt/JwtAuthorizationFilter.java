package kr.co.quiz.global.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class JwtAuthorizationFilter extends GenericFilterBean {
    private final JwtProvider jwtProvider;
    public JwtAuthorizationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    /**
     * doFilter: 요청과 응답을 가로채서 사용자의 인증 및 권한 부여
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = resolveToken((HttpServletRequest) request);

        // token이 유효하면 Authentication 객체를 만들어서 현재 사용자로 설정
        if (token != null && jwtProvider.validationToken(token)) {
            Authentication authentication = jwtProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

    /**
     * resolveToken: JWT 토큰 추출
     */
    private String resolveToken(HttpServletRequest request) {
        // Authorization 헤더에는 토큰 값이 포함되어있음
        String bearerToken = request.getHeader("Authorization");

        // "Bearer" 이후 문자열인 토큰값을 추출하여 반환
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {

            return bearerToken.substring(7);
        }

        return null;
    }
}
