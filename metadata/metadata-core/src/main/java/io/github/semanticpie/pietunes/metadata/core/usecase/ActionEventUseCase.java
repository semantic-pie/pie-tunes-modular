package io.github.semanticpie.pietunes.metadata.core.usecase;

import io.github.semanticpie.pietunes.exceptions.ActionEventException;
import io.github.semanticpie.pietunes.metadata.api.ActionEventApi;
import io.github.semanticpie.pietunes.metadata.api.model.dto.ActionEventDto;
import io.github.semanticpie.pietunes.metadata.core.repositories.UserNeo4jRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ActionEventUseCase implements ActionEventApi {

  private final UserNeo4jRepository userNeo4jRepository;

  @Override
  @Transactional
  public Mono<Void> proceedEvent(ActionEventDto event, String userID) {
    switch (event.getType()) {
      case LIKE_ENTITY -> {
        return likeEntityEvent(event.getEntityUuid(), userID);
      }
      case REMOVE_LIKE -> {
        return removeLikeEvent(event.getEntityUuid(), userID);
      }
      default -> {
        return Mono.empty();
      }
    }
  }

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
