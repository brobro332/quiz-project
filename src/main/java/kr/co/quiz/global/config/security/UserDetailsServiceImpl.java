package kr.co.quiz.global.config.security;

import kr.co.quiz.user.entity.User;
import kr.co.quiz.user.entity.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * loadUserByUsername: 사용자 정보 검색, 인증, 그리고 반환
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findOptionalByUsername(username)
                .orElseThrow(() -> {
                   return new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다.");
                });

        return new UserDetailsDTO(user);
    }
}
