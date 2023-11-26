package kr.co.quiz.game;

import kr.co.quiz.game.dtos.GameReqDTO;
import kr.co.quiz.game.entity.Game;
import kr.co.quiz.game.entity.GameRepository;
import kr.co.quiz.user.UserService;
import kr.co.quiz.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final UserService userService;

    /** [createGame]
     * 1. 회원 조회
     * 2. 퀴즈 게임 생성
     * 3. 데이터베이스에 저장
     */
    @Transactional
    public void createGame(GameReqDTO gameReqDTO) {
        // 회원 조회
        User user = userService.selectUser(gameReqDTO.getUsername());

        // 퀴즈 게임 생성
        Game game = Game.builder()
                .title(gameReqDTO.getTitle())
                .description(gameReqDTO.getDescription())
                .user(user)
                .build();

        // 데이터베이스에 저장
        gameRepository.save(game);
    }
}
