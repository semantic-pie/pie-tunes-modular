package io.github.semanticpie.pietunes.uploader.utils;

import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.MusicTrackDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class TrackMetadataParseResult {
    private final MusicTrackDto musicTrack;
    private final byte[] cover;
    private final String coverMimeType;
}
