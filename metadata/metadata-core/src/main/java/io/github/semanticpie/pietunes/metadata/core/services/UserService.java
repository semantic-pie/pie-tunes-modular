package io.github.semanticpie.pietunes.metadata.core.services;

import io.github.semanticpie.pietunes.metadata.core.models.UserNeo4j;
import java.util.Set;
import java.util.UUID;
import reactor.core.publisher.Mono;


public interface UserService {

    Mono<UserNeo4j> saveUserNeo4j(UUID userUuid);

    Mono<UserNeo4j> addPreferredGenres(Set<String> preferredGenres, UUID userUuid);

    Mono<Void> likeEntityEvent(String trackUuid, String userUuid);

    Mono<Void> removeLikeEvent(String trackUuid, String userUuid);
}
