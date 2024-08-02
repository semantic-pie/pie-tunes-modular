package io.github.semanticpie.pietunes.metadata.api;

import java.util.Set;
import reactor.core.publisher.Mono;

public interface UserApi {

  Mono<String> addUser(String id);

  Mono<String> addPreferredGenres(Set<String> preferredGenres, String userId);
}
