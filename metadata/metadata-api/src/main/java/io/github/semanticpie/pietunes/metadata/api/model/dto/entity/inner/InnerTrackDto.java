package io.github.semanticpie.pietunes.metadata.api.model.dto.entity.inner;


import java.util.UUID;


public record InnerTrackDto(UUID uuid, String title, String releaseYear, Integer bitrate, Long lengthInMilliseconds) {


}
