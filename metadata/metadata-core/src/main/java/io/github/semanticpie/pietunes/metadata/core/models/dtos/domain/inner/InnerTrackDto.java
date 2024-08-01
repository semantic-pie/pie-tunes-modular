package io.github.semanticpie.pietunes.metadata.core.models.dtos.domain.inner;


import java.util.UUID;


public record InnerTrackDto(UUID uuid, String title, String releaseYear, Integer bitrate, Long lengthInMilliseconds) {


}
