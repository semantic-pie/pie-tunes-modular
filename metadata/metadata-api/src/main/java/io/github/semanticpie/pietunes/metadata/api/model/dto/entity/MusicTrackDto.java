package io.github.semanticpie.pietunes.metadata.api.model.dto.entity;

import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.inner.InnerAlbumDto;
import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.inner.InnerBandDto;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

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

    private Set<String> genres;

    private Boolean isLiked;

    private InnerAlbumDto musicAlbum;

    private InnerBandDto musicBand;
}
