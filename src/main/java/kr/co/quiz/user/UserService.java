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
     * join: 회원가입에 대한 비즈니스 로직
     */
    @Transactional
    public void join(UserReqDTO userReqDTO) {
        Optional<User> joinedUser = userRepository.findOptionalByUsername(userReqDTO.getUsername());

        if (joinedUser.isPresent()) {

            return;
        }

        User user = User.builder()
                .username(userReqDTO.getUsername())
                .password(passwordEncoder.encode(userReqDTO.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
    }

    /**
     * login: 로그인에 대한 비즈니스 로직
     */
    @Transactional
    public JwtToken login(UserReqDTO userReqDTO) {
        Optional<User> joinedUser = userRepository.findOptionalByUsername(userReqDTO.getUsername());

        if (joinedUser.isEmpty()) {
            throw new IllegalArgumentException("해당 사용자가 존재하지 않습니다.");
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userReqDTO.getUsername(), userReqDTO.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        JwtToken token = jwtProvider.generateJwtToken(authentication);

        // 로그인할 때마다 refreshToken 갱신
        joinedUser.get().setRefreshToken(token.getRefreshToken());

        return token;
    }
}
