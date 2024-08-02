package io.github.semanticpie.pietunes.authorization.model.entity;


import io.github.semanticpie.pietunes.authorization.model.enums.UserRole;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class UserSql {

    @Id
    private UUID uuid;

    @NonNull
    private String username;

    @NonNull
    private String email;

    @NonNull
    private String password;

    @NonNull
    private UserRole role;

}
