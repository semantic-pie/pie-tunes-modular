package io.github.semanticpie.pietunes.metadata.api.model.dto.entity;

import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.inner.InnerBandDto;
import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.inner.InnerTrackDto;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

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
