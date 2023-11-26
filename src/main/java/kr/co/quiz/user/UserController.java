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

    /** [join] 
     * 1. DTO를 통해 클라이언트 요청에 대한 회원가입 데이터를 전달 받음
     * 2. Service에 DTO 전달
     * 3. 정상적인 수행 여부 응답 반환
     */
    @PostMapping
    public ResponseEntity<?> join(@RequestBody UserReqDTO userReqDTO) {

        userService.join(userReqDTO);

        return ResponseEntity.ok("회원가입에 성공했습니다.");
    }

    /** [login]
     * 1. DTO를 통해 클라이언트 요청에 대한 로그인 데이터를 전달 받음
     * 2. Service에 DTO 전달
     * 3. 정상적인 수행 여부 응답과 토큰 반환
     */
    @PostMapping("/login")
    public ResponseEntity<JwtToken> login(@RequestBody UserReqDTO userReqDTO) {
        JwtToken token = userService.login(userReqDTO);

        return ResponseEntity.ok(token);
    }
}
