package io.github.semanticpie.pietunes.metadata.core.models.dtos.domain;

import io.github.semanticpie.pietunes.metadata.core.models.dtos.domain.inner.InnerBandDto;
import io.github.semanticpie.pietunes.metadata.core.models.dtos.domain.inner.InnerTrackDto;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;

@Getter
@Setter
@NoArgsConstructor
public class MusicAlbumDto {

    @Id
    private UUID uuid;

    private String name;

    private String description;

    private int yearOfRecord;

    private Boolean isLiked;

    private InnerBandDto musicBand;

    private Set<InnerTrackDto> tracks;
}
