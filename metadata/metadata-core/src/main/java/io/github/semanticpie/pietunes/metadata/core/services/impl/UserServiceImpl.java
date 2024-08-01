package io.github.semanticpie.pietunes.metadata.core.services.impl;

import io.github.semanticpie.pietunes.metadata.core.models.UserNeo4j;
import io.github.semanticpie.pietunes.metadata.core.services.exceptions.ActionEventException;
import io.github.semanticpie.pietunes.metadata.core.models.MusicGenre;
import io.github.semanticpie.pietunes.metadata.core.repositories.MusicGenreRepository;
import io.github.semanticpie.pietunes.metadata.core.repositories.UserNeo4jRepository;
import io.github.semanticpie.pietunes.metadata.core.services.UserService;
import jakarta.transaction.Transactional;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserNeo4jRepository userNeo4jRepository;
    private final MusicGenreRepository musicGenreRepository;

    @Override
    @Transactional
    public Mono<UserNeo4j> saveUserNeo4j(UUID uuid) {
        return userNeo4jRepository.save(new UserNeo4j(uuid))
                .doOnSuccess(savedUser -> log.info("Saved to Neo4j database User with UUID: {}", savedUser.getUuid()));
    }

    @Override
    @Transactional
    public Mono<UserNeo4j> addPreferredGenres(Set<String> preferredGenres, UUID uuid) {
        return userNeo4jRepository.findUserNeo4jByUuid(uuid)
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
                });
    }

    @Override
    @Transactional
    public Mono<Void> likeEntityEvent(String entityUuid, String userUuid) {
        return userNeo4jRepository.isLikeRelationExists(entityUuid, userUuid)
                .flatMap(isLikeExists -> {
                    if (!isLikeExists) {
                        return userNeo4jRepository.likeExistingTrack(entityUuid, userUuid)
                                .then();
                    } else {
                        String errorMessage = String.format("User already 'LIKES' entity '%s'", entityUuid);
                        return Mono.error(new ActionEventException(errorMessage));
                    }
                });

    }

    @Override
    @Transactional
    public Mono<Void> removeLikeEvent(String trackUuid, String userUuid) {
        return userNeo4jRepository.isLikeRelationExists(trackUuid, userUuid)
                .flatMap(isLikeExists -> {
                    if (isLikeExists) {
                        return userNeo4jRepository.deleteLikeRelation(trackUuid, userUuid)
                                .then();
                    } else {
                        String errorMessage = String.format("User doesn't 'LIKE' entity '%s'", trackUuid);
                        return Mono.error(new ActionEventException(errorMessage));
                    }
                });

    }


}





