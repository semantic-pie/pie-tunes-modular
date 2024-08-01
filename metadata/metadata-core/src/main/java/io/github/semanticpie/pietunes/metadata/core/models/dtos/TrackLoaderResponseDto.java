package io.github.semanticpie.pietunes.metadata.core.models.dtos;


import io.github.semanticpie.pietunes.metadata.core.models.dtos.domain.MusicTrackDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TrackLoaderResponseDto {
    private final MusicTrackDto uploadedTrack;
}
