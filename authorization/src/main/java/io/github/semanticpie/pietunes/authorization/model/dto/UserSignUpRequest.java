package io.github.semanticpie.pietunes.authorization.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class UserSignUpRequest {
    private String email;
    private String username;
    private String password;

}
