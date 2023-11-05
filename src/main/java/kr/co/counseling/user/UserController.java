package kr.co.counseling.user;

import kr.co.counseling.global.config.jwt.JwtToken;
import kr.co.counseling.user.dtos.UserReqDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * join: 회원가입에 대한 HTTP 요청 처리 및 응답 생성
     */
    @PostMapping
    public ResponseEntity<?> join(@RequestBody UserReqDTO userReqDTO) {

        userService.join(userReqDTO);

        return ResponseEntity.ok("회원가입에 성공했습니다.");
    }

    /**
     * login: 로그인에 대한 HTTP 요청 처리 및 응답 생성
     */
    @PostMapping("/login")
    public ResponseEntity<JwtToken> login(@RequestBody UserReqDTO userReqDTO) {

        JwtToken token = userService.login(userReqDTO);

        return ResponseEntity.ok(token);
    }
}
