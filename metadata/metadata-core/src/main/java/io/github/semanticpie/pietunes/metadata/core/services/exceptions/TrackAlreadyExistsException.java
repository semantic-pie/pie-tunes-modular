package io.github.semanticpie.pietunes.metadata.core.services.exceptions;

public class TrackAlreadyExistsException extends RuntimeException {
    public TrackAlreadyExistsException(String message) {
        super(message);
    }
}
