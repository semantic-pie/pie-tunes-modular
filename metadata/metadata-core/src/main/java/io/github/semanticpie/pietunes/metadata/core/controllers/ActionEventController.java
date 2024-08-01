package io.github.semanticpie.pietunes.metadata.core.controllers;

import io.github.semanticpie.pietunes.metadata.core.models.dtos.ActionEventDto;
import io.github.semanticpie.pietunes.metadata.core.services.UserService;
import io.github.semanticpie.pietunes.metadata.core.services.jwt.JwtTokenProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@RestController
@Slf4j
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class ActionEventController {

    private final UserService userService;
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/api/tracks/events")
    public Mono<Void> likeTrack(@RequestBody ActionEventDto event, ServerWebExchange exchange) {

        String jwtToken = jwtTokenProvider.getJwtTokenFromRequest(exchange.getRequest());
        String userUuid = jwtTokenProvider.getUUID(jwtToken);

        switch (event.getType()) {
            case LIKE_ENTITY -> {
                return userService.likeEntityEvent(event.getEntityUuid(), userUuid);
            }
            case REMOVE_LIKE -> {
                return userService.removeLikeEvent(event.getEntityUuid(), userUuid);
            }
            default -> {
                return Mono.empty();
            }
        }
    }
}
