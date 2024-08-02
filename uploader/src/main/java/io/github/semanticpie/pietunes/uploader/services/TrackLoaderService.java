package io.github.semanticpie.pietunes.uploader.services;

import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.MusicTrackDto;
import java.util.List;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface TrackLoaderService {
    Mono<MusicTrackDto> save(FilePart filePart);

    Mono<Void> saveAll(List<FilePart> fileParts);
}
