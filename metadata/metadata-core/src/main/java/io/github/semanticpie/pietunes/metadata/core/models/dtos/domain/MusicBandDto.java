package io.github.semanticpie.pietunes.metadata.core.models.dtos.domain;

import io.github.semanticpie.pietunes.metadata.core.models.dtos.domain.inner.InnerAlbumDto;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;

@Getter
@Setter
@NoArgsConstructor
public class MusicBandDto {

    @Id
    private UUID uuid;

    private String name;

    private String description;

    private Boolean isLiked;

    private Set<InnerAlbumDto> albums;
}
