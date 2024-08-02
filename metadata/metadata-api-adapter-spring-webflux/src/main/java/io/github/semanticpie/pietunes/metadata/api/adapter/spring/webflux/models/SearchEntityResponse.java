package io.github.semanticpie.pietunes.metadata.api.adapter.spring.webflux.models;


import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.MusicAlbumDto;
import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.MusicBandDto;
import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.MusicTrackDto;
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
