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

    /**
     * @implNote 회원 조회 및 퀴즈게임 생성
     * @param create title, description, username
     */
    @Transactional
    public void createGame(GameReqDTO.CREATE create) {
        // 회원 조회
        User user = userService.selectUser(create.getUsername());

        // 퀴즈게임 생성
        Game game = Game.builder()
                .title(create.getTitle())
                .description(create.getDescription())
                .user(user)
                .build();

        // 데이터베이스에 저장
        gameRepository.save(game);
    }

    /**
     * @implNote 퀴즈게임 조회 및 수정
     * @param update id, title, description
     */
    @Transactional
    public void updateGame(GameReqDTO.UPDATE update) {
        // id를 통해 퀴즈게임 조회
        Game game = gameRepository.findOptionalById(update.getId())
                .orElseThrow(()->{
                    return new IllegalArgumentException("해당 퀴즈게임을 찾을 수 없습니다.");
                });

        // id로 조회한 퀴즈게임이 존재한다면
        if (game != null) {
            game = Game.builder()
                    .title(update.getTitle())
                    .description(update.getDescription())
                    .build();
        }
    }

    /**
     * @implNote 퀴즈게임 조회 및 삭제
     * @param delete id
     */
    @Transactional
    public void deleteGame(GameReqDTO.DELETE delete) {
        // id를 통해 퀴즈게임 조회
        Game game = gameRepository.findOptionalById(delete.getId())
                .orElseThrow(()->{
                    return new IllegalArgumentException("해당 퀴즈게임을 찾을 수 없습니다.");
                });

        // id로 조회한 퀴즈게임이 존재한다면
        if (game != null) {
            gameRepository.delete(game);
        }
    }
}
