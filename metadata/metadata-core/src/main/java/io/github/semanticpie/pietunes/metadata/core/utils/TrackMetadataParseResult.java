package io.github.semanticpie.pietunes.metadata.core.utils;

import io.github.semanticpie.pietunes.metadata.core.models.MusicTrack;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class TrackMetadataParseResult {
    private final MusicTrack musicTrack;
    private final byte[] cover;
    private final String coverMimeType;
}
