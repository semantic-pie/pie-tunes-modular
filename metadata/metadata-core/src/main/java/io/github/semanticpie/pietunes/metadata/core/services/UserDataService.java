package io.github.semanticpie.pietunes.metadata.core.services;

import io.github.semanticpie.pietunes.metadata.api.UserApi;
import io.github.semanticpie.pietunes.metadata.core.models.MusicGenre;
import io.github.semanticpie.pietunes.metadata.core.models.UserNeo4j;
import io.github.semanticpie.pietunes.metadata.core.repositories.MusicGenreRepository;
import io.github.semanticpie.pietunes.metadata.core.repositories.UserNeo4jRepository;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDataService implements UserApi {

  private final UserNeo4jRepository userNeo4jRepository;
  private final MusicGenreRepository musicGenreRepository;

  @Override
  public Mono<String> addUser(String id) {
    return userNeo4jRepository.save(new UserNeo4j(UUID.fromString(id)))
        .doOnSuccess(savedUser -> log.info("Saved to Neo4j database User with UUID: {}",
            savedUser.getUuid()))
        .map(userNeo4j -> userNeo4j.getUuid().toString());
  }

  @Override
  public Mono<String> addPreferredGenres(Set<String> preferredGenres, String userId) {
    return userNeo4jRepository.findUserNeo4jByUuid(UUID.fromString(userId))
        .flatMap(existingUser -> {
          Flux<MusicGenre> musicGenreFlux = Flux.fromIterable(preferredGenres)
              .flatMap(genre -> musicGenreRepository.persist(new MusicGenre(genre)));

          return musicGenreFlux.collectList()
              .flatMap(persistedGenres -> {
                persistedGenres.forEach(existingUser::addPreferredGenre);
                return userNeo4jRepository.save(existingUser)
                    .doOnSuccess(updatedUser ->
                        log.info("Add genres: {} to User with UUID: {}",
                            persistedGenres, updatedUser.getUuid()));
              });
        }).map(userNeo4j -> userNeo4j.getUuid().toString());
  }
}
