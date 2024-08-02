package io.github.semanticpie.pietunes.metadata.core.services.impl;

import io.github.semanticpie.pietunes.jwt.token.provider.JwtTokenProvider;
import io.github.semanticpie.pietunes.jwt.token.provider.models.User;
import io.github.semanticpie.pietunes.metadata.core.models.JwtResponse;
import io.github.semanticpie.pietunes.metadata.core.models.UserSql;
import io.github.semanticpie.pietunes.metadata.core.models.dtos.UserSignUpRequest;
import io.github.semanticpie.pietunes.metadata.core.models.enums.UserRole;
import io.github.semanticpie.pietunes.metadata.core.repositories.UserH2Repository;
import io.github.semanticpie.pietunes.metadata.core.services.AuthService;
import io.github.semanticpie.pietunes.metadata.core.services.exceptions.UserAlreadyExistsException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Mono;


@Service
@Slf4j
@AllArgsConstructor
@EnableWebFlux
public class AuthServiceImpl implements AuthService {

  private final ReactiveAuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final BCryptPasswordEncoder passwordEncoder;
  private final UserH2Repository userH2Repository;

  private final UserRole DEFAULT_USER_ROLE = UserRole.ROLE_USER;

  @Override
  @Transactional
  public Mono<UserSql> saveUserSql(UserSignUpRequest creationRequest) {
    return userH2Repository.findUserSqlByEmail(creationRequest.getEmail())
        .flatMap(existingUser -> {
          String errorMessage = String.format("User with email '%s' already exists.",
              existingUser.getEmail());
          log.info(errorMessage);
          return Mono.error(new UserAlreadyExistsException(errorMessage));
        })
        .switchIfEmpty(Mono.defer(() -> saveUserToSql(creationRequest)))
        .cast(UserSql.class);

  }

  private Mono<UserSql> saveUserToSql(UserSignUpRequest request) {
    String hashedPassword = passwordEncoder.encode(request.getPassword());
    var userToSave = new UserSql(
        request.getUsername(),
        request.getEmail(),
        hashedPassword,
        DEFAULT_USER_ROLE
    );
    return userH2Repository.save(userToSave)
        .doOnSuccess(
            savedUser -> log.info("Saved to SQL database User with UUID: {}", savedUser.getUuid()));
  }

  @Override
  public Mono<ResponseEntity<JwtResponse>> authenticate(String email, String password) {
    return authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(email, password))
        .flatMap(authentication ->
            userH2Repository.findUserSqlByEmail(email)
                .map(user -> {
                  String jwt = jwtTokenProvider.generateToken(
                      new User(user.getUuid(), user.getEmail(), user.getUsername(),
                          user.getPassword(), user.getRole().toString()));
                  HttpHeaders httpHeaders = new HttpHeaders();
                  httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
                  var tokenResponse = new JwtResponse(jwt);
                  return ResponseEntity.ok().headers(httpHeaders).body(tokenResponse);
                })
        )
        .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
  }

}
