package io.github.semanticpie.pietunes.authorization.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserCredentialsDto {
    private String email;
    private String username;
}
