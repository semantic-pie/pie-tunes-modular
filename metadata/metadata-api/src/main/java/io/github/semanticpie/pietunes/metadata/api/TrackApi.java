package io.github.semanticpie.pietunes.metadata.api;

import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.MusicTrackDto;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TrackApi {

  Mono<MusicTrackDto> findByNameAndBandName(String name, String albumName);

  Flux<MusicTrackDto> findAllTracks(Pageable pageable);

  Flux<MusicTrackDto> findAllTracksByName(String userId, String name);

  Flux<MusicTrackDto> findAllLikedTracks(String userId, Pageable pageable);

  Mono<MusicTrackDto> findMusicTrackById(String id);

  Mono<Long> findTotalLikedTracks(String userId);

  Mono<Long> findTotalLikedTracksByTitle(String title, String userId);

  Flux<MusicTrackDto> findAllLikedTracksByTitle(String title, String userId, Pageable pageable);

  Mono<Long> findTotalTracksInAlbumByUuid(String albumId);

  Flux<MusicTrackDto> findTracksByAlbumUuid(String albumId);

  Mono<MusicTrackDto> saveTrack(MusicTrackDto musicTrackDto);

}
