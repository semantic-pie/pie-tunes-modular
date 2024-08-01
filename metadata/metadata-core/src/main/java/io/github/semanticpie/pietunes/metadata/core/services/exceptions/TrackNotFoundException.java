package io.github.semanticpie.pietunes.metadata.core.services.exceptions;

public class TrackNotFoundException extends RuntimeException {
    public TrackNotFoundException(String message) {
        super(message);
    }
}
