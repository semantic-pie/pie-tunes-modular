package io.github.semanticpie.pietunes.metadata.api.model.dto.entity.inner;


import java.util.UUID;

public record InnerAlbumDto(UUID uuid, String name, String description, int yearOfRecord) {
}
