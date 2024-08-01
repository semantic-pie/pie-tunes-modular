package io.github.semanticpie.pietunes.metadata.core.services;

import io.github.semanticpie.pietunes.metadata.core.models.JwtResponse;
import io.github.semanticpie.pietunes.metadata.core.models.UserSql;
import io.github.semanticpie.pietunes.metadata.core.models.dtos.UserSignUpRequest;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface AuthService {

    Mono<UserSql> saveUserSql(UserSignUpRequest creationRequest);

    Mono<ResponseEntity<JwtResponse>> authenticate(String email, String password);

}
