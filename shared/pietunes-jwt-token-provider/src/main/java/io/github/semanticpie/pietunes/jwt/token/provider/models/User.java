package io.github.semanticpie.pietunes.jwt.token.provider.models;

import java.util.UUID;

public record User (UUID uuid, String email, String username, String password, String role) {

}
