package io.github.semanticpie.pietunes.metadata.api.adapter.spring.webflux.controllers;

import io.github.semanticpie.pietunes.jwt.token.provider.JwtTokenProvider;
import io.github.semanticpie.pietunes.metadata.api.UserApi;
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

  private final UserApi userApi;
  private final JwtTokenProvider jwtTokenProvider;

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<ResponseEntity<Void>> addUser(@RequestBody UUID userUuid) {
    return userApi.addUser(String.valueOf(userUuid)).then(
        Mono.just(new ResponseEntity<>(HttpStatus.CREATED))
    );
  }

  @PostMapping(value = "/addGenres", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<ResponseEntity<Void>> addPreferredGenresToUser(
      @RequestBody Set<String> preferredGenres,
      ServerWebExchange exchange) {

    String jwtToken = jwtTokenProvider.getJwtTokenFromRequest(exchange.getRequest());
    String userUuid = jwtTokenProvider.getUUID(jwtToken);

    return userApi.addPreferredGenres(preferredGenres, userUuid).then(
        Mono.just(new ResponseEntity<>(HttpStatus.CREATED))
    );
  }


}
