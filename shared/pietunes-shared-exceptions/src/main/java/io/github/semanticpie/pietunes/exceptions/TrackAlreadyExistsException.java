package io.github.semanticpie.pietunes.exceptions;

public class TrackAlreadyExistsException extends RuntimeException {
    public TrackAlreadyExistsException(String message) {
        super(message);
    }
}
