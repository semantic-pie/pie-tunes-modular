package io.github.semanticpie.pietunes.metadata.api.model.dto.entity;

import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.inner.InnerAlbumDto;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

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
