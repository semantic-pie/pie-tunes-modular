package io.github.semanticpie.pietunes.metadata.core.models.dtos.domain;

import io.github.semanticpie.pietunes.metadata.core.models.dtos.domain.inner.InnerAlbumDto;
import io.github.semanticpie.pietunes.metadata.core.models.dtos.domain.inner.InnerBandDto;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;

@Getter
@Setter
@NoArgsConstructor
public class MusicTrackDto {

    @Id
    private UUID uuid;

    private String title;

    private String releaseYear;

    private Integer bitrate;

    private Long lengthInMilliseconds;

    private Boolean isLiked;

    private InnerAlbumDto musicAlbum;

    private InnerBandDto musicBand;
}
