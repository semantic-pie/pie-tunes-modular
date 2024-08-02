package io.github.semanticpie.pietunes.metadata.api.adapter.spring.webflux.controllers;

import io.github.semanticpie.pietunes.jwt.token.provider.JwtTokenProvider;
import io.github.semanticpie.pietunes.metadata.api.ActionEventApi;
import io.github.semanticpie.pietunes.metadata.api.model.dto.ActionEventDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CookieValue;
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

    private final ActionEventApi actionEventApi;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/api/tracks/events")
    public Mono<Void> likeTrack(@RequestBody ActionEventDto event, ServerWebExchange exchange ) {
        String jwtToken = jwtTokenProvider.getJwtTokenFromRequest(exchange.getRequest());
        String userUuid = jwtTokenProvider.getUUID(jwtToken);

        return actionEventApi.proceedEvent(event, userUuid);
    }
}
