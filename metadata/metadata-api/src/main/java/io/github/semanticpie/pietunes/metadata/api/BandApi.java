package io.github.semanticpie.pietunes.metadata.api;

import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.MusicBandDto;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BandApi {

  Flux<MusicBandDto> findAllBandsByName(String userId, String name);

  Flux<MusicBandDto> getAllBands(Pageable pageable);

  Mono<MusicBandDto> getBandById(String bandId);

  Flux<MusicBandDto> getAllUserLikedBands(String userId, Pageable pageable);

  Flux<MusicBandDto> getAllUserLikedBandsByTitle(String title, String userId, Pageable pageable);

  Mono<Long> countUserLikedBands(String userId);

  Mono<Long> countUserLikedBandsByTitle(String userId, String title);
}
