package kr.co.quiz.global.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.xml.bind.DatatypeConverter;
import kr.co.quiz.user.entity.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtProvider {
    private final Key key;
    private final UserRepository userRepository;
    private static final long ACCESS_TOKEN_VALIDATION_SECONDS = 1000L * 60 * 30; // 1. AccessToken: 30분
    private static final long REFRESH_TOKEN_VALIDATION_SECONDS = 1000L * 60 * 60 * 24; // 2. RefreshToken: 1주

    /**
     * JwtProvider: JWT의 서명을 생성 및 검증하기 위한 Key 생성
     * 1. SECRET_KEY 값을 Base64로 인코딩된 바이트 배열로 변환
     * 2. 바이트 배열 값을 HMAC-SHA 알고리즘을 사용하여 서명 키를 생성
     */
    public JwtProvider(@Value("${SECRET_KEY}") String SECRET_KEY, UserRepository userRepository) {
        this.userRepository = userRepository;
        byte[] secretByteKey = DatatypeConverter.parseBase64Binary(SECRET_KEY);
        this.key = Keys.hmacShaKeyFor(secretByteKey);
    }

    /**
     * generateJwtToken: JWT 토큰 생성
     */
    public JwtToken generateJwtToken(Authentication authentication) {

        // 사용자의 권한을 추출하여 쉼표로 구분된 문자열로 변환
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // accessToken 생성
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDATION_SECONDS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // refreshToken 생성
        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDATION_SECONDS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // JWT 빌드
        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String generateAccessToken(UserDetails user) {

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("auth", user.getAuthorities())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDATION_SECONDS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /* ====================================================================== */

    /**
     * getAuthentication: 토큰의 사용자 이름과 Claims 헤더의 "auth" 값을 통해 Authentication 객체 생성 및 반환
     */
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {

            throw new RuntimeException("토큰에 권한 정보가 없습니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails user = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(user, "", authorities);
    }

    /**
     * validateToken: token의 유효성을 검증 후 예외처리
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token);

            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty", e);
        }

        return false;
    }

    /**
     * parseClaims: token의 유효성을 검사한 후 유효하면 Claims 반환
     */
    public Claims parseClaims(String token) {
        try {

            return Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {

            return e.getClaims();
        }
    }

    /**
     * resolveToken: JWT 토큰 추출
     */
    public String resolveAccessToken(HttpServletRequest request) {
        // Authorization 헤더에는 토큰 값이 포함되어있음
        if(request.getHeader("Authorization") != null ) {
            return request.getHeader("Authorization").substring(7);
        }

        return null;
    }

    /**
     * resolveRefreshToken: Request의 Header에서 refreshToken 값을 가져옴
     */
    public String resolveRefreshToken(HttpServletRequest request) {
        if(request.getHeader("refreshToken") != null ) {
            return request.getHeader("refreshToken").substring(7);
        }

        return null;
    }

    public boolean compareRefreshToken(String username, String refreshToken) {
        kr.co.quiz.user.entity.User user = userRepository.findOptionalByUsername(username)
                .orElseThrow(()->{
                    return new IllegalArgumentException("해당 사용자를 찾을 수 없습니다.");
                });

        boolean flag = false;
        if (user != null) {
            String storedRefreshToken = user.getRefreshToken();

            if (refreshToken.equals(storedRefreshToken)) {
                flag = true;
            }
        }

        return flag;
    }
}
