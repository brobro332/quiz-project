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

    public JwtProvider(@Value("${SECRET_KEY}") String SECRET_KEY, UserRepository userRepository) {
        this.userRepository = userRepository;
        byte[] secretByteKey = DatatypeConverter.parseBase64Binary(SECRET_KEY);
        this.key = Keys.hmacShaKeyFor(secretByteKey);
    }

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

    public Claims parseClaims(String token) {
        try {

            return Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {

            return e.getClaims();
        }
    }

    public String resolveAccessToken(HttpServletRequest request) {
        // Authorization 헤더에는 토큰 값이 포함되어있음
        if(request.getHeader("Authorization") != null ) {
            return request.getHeader("Authorization").substring(7);
        }

        return null;
    }

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
