package io.github.semanticpie.pietunes.metadata.api;


import io.github.semanticpie.pietunes.metadata.api.model.dto.ActionEventDto;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface ActionEventApi {

  Mono<Void> proceedEvent(ActionEventDto event, String userID);
}
