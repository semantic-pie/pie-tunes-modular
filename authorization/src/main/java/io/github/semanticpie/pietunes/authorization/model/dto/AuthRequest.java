package io.github.semanticpie.pietunes.authorization.model.dto;

public record AuthRequest(String email,
                          String password) {
}