package io.github.semanticpie.pietunes.metadata.core.controllers;

import io.github.semanticpie.pietunes.metadata.core.models.JwtResponse;
import io.github.semanticpie.pietunes.metadata.core.models.dtos.AuthRequest;
import io.github.semanticpie.pietunes.metadata.core.models.dtos.UserSignUpRequest;
import io.github.semanticpie.pietunes.metadata.core.services.AuthService;
import io.github.semanticpie.pietunes.metadata.core.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping(path = "/api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<JwtResponse>> createUser(@RequestBody UserSignUpRequest request) {
        return authService.saveUserSql(request)
                .flatMap(savedSql -> userService.saveUserNeo4j(savedSql.getUuid()))
                .then(authService.authenticate(request.getEmail(), request.getPassword()));
    }


    @PostMapping("/login")
    public Mono<ResponseEntity<JwtResponse>> authentication(@RequestBody AuthRequest authRequest) {
        return authService.authenticate(authRequest.email(), authRequest.password());
    }



}
