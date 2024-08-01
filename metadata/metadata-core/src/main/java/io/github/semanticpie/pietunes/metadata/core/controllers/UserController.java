package io.github.semanticpie.pietunes.metadata.core.controllers;

import io.github.semanticpie.pietunes.metadata.core.models.JwtResponse;
import io.github.semanticpie.pietunes.metadata.core.models.dtos.UserDto;
import io.github.semanticpie.pietunes.metadata.core.services.UserService;
import io.github.semanticpie.pietunes.metadata.core.services.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@AllArgsConstructor
@RequestMapping(path = "/api/v1/domain/users")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<String>> saveUserUuid(@RequestBody UUID userUuid) {
        return userService.saveUserNeo4j(userUuid)
                .map(savedUser -> {
                    String responseMessage =
                            String.format("Saved to Neo4j database User with UUID '%s'", savedUser.getUuid());
                    return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
                });


    }

    @GetMapping
    public Mono<ResponseEntity<UserDto>> getUser(ServerWebExchange exchange) {
        JwtResponse jwtToken = new JwtResponse(jwtTokenProvider.getJwtTokenFromRequest(exchange.getRequest()));
        Jws<Claims> tokenPayload = jwtTokenProvider.getAllClaimsFromToken(jwtToken.getAccessToken());

        String username = tokenPayload.getBody().get("username", String.class);
        String email = tokenPayload.getBody().get("email", String.class);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken.getAccessToken());

        return Mono.just(ResponseEntity.ok()
                .headers(httpHeaders)
                .body(new UserDto(email, username)));

    }

    @PostMapping(value = "/addGenres", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<String>> addPreferredGenresToUser(@RequestBody Set<String> preferredGenres,
                                                                 ServerWebExchange exchange) {

        String jwtToken = jwtTokenProvider.getJwtTokenFromRequest(exchange.getRequest());
        String userUuid = jwtTokenProvider.getUUID(jwtToken);

        return userService.addPreferredGenres(preferredGenres, UUID.fromString(userUuid))
                .map(updatedUser -> {
                    String responseMessage =
                            String.format("Add genres: '%s' to User with UUID: '%s'", preferredGenres, updatedUser.getUuid());
                    return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
                });
    }


}
