package kr.co.quiz.user.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserReqDTO {
    private String username;
    private String password;

}
