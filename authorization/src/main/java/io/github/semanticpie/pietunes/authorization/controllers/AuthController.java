package io.github.semanticpie.pietunes.authorization.controllers;


import io.github.semanticpie.pietunes.authorization.model.JwtResponse;
import io.github.semanticpie.pietunes.authorization.model.dto.AuthRequest;
import io.github.semanticpie.pietunes.authorization.model.dto.UserCredentialsDto;
import io.github.semanticpie.pietunes.authorization.model.dto.UserSignUpRequest;
import io.github.semanticpie.pietunes.authorization.service.AuthService;
import io.github.semanticpie.pietunes.jwt.token.provider.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping(path = "/api/v1/")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/auth/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<JwtResponse>> createUser(@RequestBody UserSignUpRequest request) {
        return authService.saveUserSql(request)
                .then(authService.authenticate(request.getEmail(), request.getPassword()));
    }


    @PostMapping("/auth/login")
    public Mono<ResponseEntity<JwtResponse>> authentication(@RequestBody AuthRequest authRequest) {
        return authService.authenticate(authRequest.email(), authRequest.password());
    }

    @GetMapping("/domain/users") //TODO Непонятно зачем нужен данный метод
    public Mono<ResponseEntity<UserCredentialsDto>> getUser(ServerWebExchange exchange) {
        JwtResponse jwtToken = new JwtResponse(jwtTokenProvider.getJwtTokenFromRequest(exchange.getRequest()));
        Jws<Claims> tokenPayload = jwtTokenProvider.getAllClaimsFromToken(jwtToken.getAccessToken());

        String username = tokenPayload.getBody().get("username", String.class);
        String email = tokenPayload.getBody().get("email", String.class);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken.getAccessToken());

        return Mono.just(ResponseEntity.ok()
            .headers(httpHeaders)
            .body(new UserCredentialsDto(email, username)));

    }



}
