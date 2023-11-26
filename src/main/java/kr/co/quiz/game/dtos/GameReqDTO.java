package kr.co.quiz.game.dtos;

import kr.co.quiz.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GameReqDTO {
    private String title;
    private String description;
    private String username;
}
