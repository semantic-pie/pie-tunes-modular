package io.github.semanticpie.pietunes.metadata.core.models.dtos;

import io.github.semanticpie.pietunes.metadata.core.models.dtos.domain.MusicAlbumDto;
import io.github.semanticpie.pietunes.metadata.core.models.dtos.domain.MusicTrackDto;
import io.github.semanticpie.pietunes.metadata.core.models.dtos.domain.MusicBandDto;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchEntityResponse {

    private List<MusicTrackDto> tracks;
    private List<MusicAlbumDto> albums;
    private List<MusicBandDto> bands;

}
