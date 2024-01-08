package kr.co.quiz.user;

import kr.co.quiz.global.config.jwt.JwtProvider;
import kr.co.quiz.global.config.jwt.JwtToken;
import kr.co.quiz.user.dtos.UserReqDTO;
import kr.co.quiz.user.entity.Role;
import kr.co.quiz.user.entity.User;
import kr.co.quiz.user.entity.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtProvider jwtProvider;

    /**
     * @implNote 사용자가 이미 가입됐는지 확인 후 아니라면 회원가입
     * @param userReqDTO username, password
     */
    @Transactional
    public void join(UserReqDTO userReqDTO) {
        // 해당 사용자가 이미 가입됐는지 확인
        Optional<User> joinedUser = userRepository.findOptionalByUsername(userReqDTO.getUsername());
        if (joinedUser.isPresent()) {
            return;
        }

        // 객체 조립
        User user = User.builder()
                .username(userReqDTO.getUsername())
                .password(passwordEncoder.encode(userReqDTO.getPassword()))
                .nickname(null)
                .role(Role.USER)
                .build();

        // 저장
        userRepository.save(user);
    }

    /**
     * @implNote 로그인 및 JwtToken 반환
     * @param userReqDTO username, password
     * @return JwtToken
     */
    @Transactional
    public JwtToken login(UserReqDTO userReqDTO) {
        // 해당 사용자가 존재하는지 확인
        Optional<User> joinedUser = userRepository.findOptionalByUsername(userReqDTO.getUsername());
        if (joinedUser.isEmpty()) {
            throw new IllegalArgumentException("해당 사용자가 존재하지 않습니다.");
        }

        // 존재한다면 로그인
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userReqDTO.getUsername(), userReqDTO.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // accessToken + refreshToken 갱신
        JwtToken token = jwtProvider.generateJwtToken(authentication);
        // refreshToken 값은 DB에 저장
        joinedUser.get().setRefreshToken(token.getRefreshToken());

        return token;
    }

    /**
     * @implNote 아이디를 통해 회원을 조회하여 User 반환
     * @param username 계정 아이디
     * @return User
     */
    @Transactional
    public User selectUser(String username) {
        return userRepository.findOptionalByUsername(username)
                .orElseThrow(()->{
                    return new IllegalArgumentException("해당 회원을 찾을 수 없습니다.");
                });
    }
}
