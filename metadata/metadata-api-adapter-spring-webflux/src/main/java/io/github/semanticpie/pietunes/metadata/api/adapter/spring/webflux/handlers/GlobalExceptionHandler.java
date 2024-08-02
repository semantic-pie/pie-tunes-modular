package io.github.semanticpie.pietunes.metadata.api.adapter.spring.webflux.handlers;

import io.github.semanticpie.pietunes.exceptions.ActionEventException;
import io.github.semanticpie.pietunes.exceptions.NodeAlreadyExists;
import io.github.semanticpie.pietunes.exceptions.TrackNotFoundException;
import io.github.semanticpie.pietunes.exceptions.UserAlreadyExistsException;
import io.github.semanticpie.pietunes.exceptions.errors.ApiPieTunesErrorInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Order(-2)
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(TrackNotFoundException.class)
    public Mono<ApiPieTunesErrorInfo> handleTrackNotFound(ServerWebExchange exchange, Exception ex) {
        log.warn(ex.getMessage());
        return Mono.just(new ApiPieTunesErrorInfo(HttpStatus.NOT_FOUND.value(),
                exchange.getRequest().getPath().toString(), ex.getMessage()));
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ExceptionHandler(NodeAlreadyExists.class)
    public Mono<ApiPieTunesErrorInfo> handleConflicts(ServerWebExchange exchange, Exception ex) {
        log.warn(ex.getMessage());
        return Mono.just(new ApiPieTunesErrorInfo(HttpStatus.CONFLICT.value(),
                exchange.getRequest().getPath().toString(), ex.getMessage()));
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ExceptionHandler(UserAlreadyExistsException.class)
    Mono<ApiPieTunesErrorInfo>
    handleUserAlreadyExistsException(ServerWebExchange exchange, Exception ex) {
        return Mono.just(new ApiPieTunesErrorInfo(HttpStatus.CONFLICT.value(),
                exchange.getRequest().getPath().toString(), ex.getMessage()));
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ActionEventException.class)
    Mono<ApiPieTunesErrorInfo>
    handleActionEventExceptionException(ServerWebExchange exchange, Exception ex) {
        return Mono.just(new ApiPieTunesErrorInfo(HttpStatus.BAD_REQUEST.value(),
                exchange.getRequest().getPath().toString(), ex.getMessage()));
    }
}