package io.github.semanticpie.pietunes.metadata.core.models.dtos.domain.inner;


import java.util.UUID;

public record InnerAlbumDto(UUID uuid, String name, String description, int yearOfRecord) {
}
