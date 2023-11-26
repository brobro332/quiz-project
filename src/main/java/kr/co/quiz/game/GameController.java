package kr.co.quiz.game;

import kr.co.quiz.game.dtos.GameReqDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/game")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    /** [createGame]
     * 1. DTO를 통해 클라이언트 요청에 대한 퀴즈게임 생성 데이터를 전달 받음
     * 2. Service에 DTO 전달
     * 3. 정상적인 수행 여부 응답 반환
     */
    @PostMapping
    public ResponseEntity<?> createGame(@RequestBody GameReqDTO gameReqDTO) {
        gameService.createGame(gameReqDTO);

        return ResponseEntity.ok("퀴즈 게임이 정상적으로 생성되었습니다.");
    }
}
