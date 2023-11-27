package kr.co.quiz.user;

import kr.co.quiz.global.config.jwt.JwtToken;
import kr.co.quiz.user.dtos.UserReqDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * @apiNote 회원가입
     * @param userReqDTO username, password
     * @return Message
     */
    @PostMapping
    public ResponseEntity<?> join(@RequestBody UserReqDTO userReqDTO) {
        userService.join(userReqDTO);

        return ResponseEntity.ok("회원가입에 성공했습니다.");
    }

    /**
     * @apiNote 로그인 및 JwtToken 응답
     * @param userReqDTO username, password
     * @return JwtToken
     */
    @PostMapping("/login")
    public ResponseEntity<JwtToken> login(@RequestBody UserReqDTO userReqDTO) {
        JwtToken token = userService.login(userReqDTO);

        return ResponseEntity.ok(token);
    }
}
