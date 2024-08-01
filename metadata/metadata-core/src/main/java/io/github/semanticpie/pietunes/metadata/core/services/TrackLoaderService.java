package io.github.semanticpie.pietunes.metadata.core.services;

import io.github.semanticpie.pietunes.metadata.core.models.MusicTrack;
import java.util.List;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface TrackLoaderService {
    Mono<MusicTrack> save(FilePart filePart);

    Mono<Void> saveAll(List<FilePart> fileParts);
}
