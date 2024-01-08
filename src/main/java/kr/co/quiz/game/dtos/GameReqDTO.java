package kr.co.quiz.game.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class GameReqDTO {
    @Getter
    @Builder
    @AllArgsConstructor
    public class CREATE {
        private String title;
        private String description;
        private String username;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public class UPDATE {
        private Long id;
        private String title;
        private String description;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public class DELETE {
        private Long id;

        public DELETE() {   }
    }
}
