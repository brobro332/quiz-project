package kr.co.quiz.game;

import kr.co.quiz.game.dtos.GameReqDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/game")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    /**
     * @apiNote 퀴즈게임 생성 컨트롤러
     * @param create title, description, username
     * @return ResponseEntity.ok
     */
    @PostMapping
    public ResponseEntity<?> createGame(@RequestBody GameReqDTO.CREATE create) {
        gameService.createGame(create);

        return ResponseEntity.ok("퀴즈 게임이 정상적으로 생성되었습니다.");
    }

    /**
     * @apiNote 퀴즈게임 수정 컨트롤러
     * @param update id, title, description
     * @return ResponseEntity.ok
     */
    @PutMapping
    public ResponseEntity<?> updateGame(@RequestBody GameReqDTO.UPDATE update) {
        gameService.updateGame(update);

        return ResponseEntity.ok("퀴즈 게임이 정상적으로 수정되었습니다.");
    }

    /**
     * @apiNote 퀴즈게임 삭제 컨트롤러
     * @param delete id
     * @return ResponseEntity.ok
     */
    @DeleteMapping
    public ResponseEntity<?> deleteGame(@RequestBody GameReqDTO.DELETE delete) {
        gameService.deleteGame(delete);

        return ResponseEntity.ok("퀴즈 게임이 정상적으로 삭제되었습니다.");
    }
}
