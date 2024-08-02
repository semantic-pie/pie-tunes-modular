package io.github.semanticpie.pietunes.authorization.service;


import io.github.semanticpie.pietunes.authorization.model.JwtResponse;
import io.github.semanticpie.pietunes.authorization.model.dto.UserSignUpRequest;
import io.github.semanticpie.pietunes.authorization.model.entity.UserSql;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface AuthService {

    Mono<UserSql> saveUserSql(UserSignUpRequest creationRequest);

    Mono<ResponseEntity<JwtResponse>> authenticate(String email, String password);

}
