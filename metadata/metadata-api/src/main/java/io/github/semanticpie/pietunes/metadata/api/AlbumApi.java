package io.github.semanticpie.pietunes.metadata.api;

import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.MusicAlbumDto;

import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AlbumApi {

  Mono<MusicAlbumDto> findAlbumByUuid(String uuid);

  Flux<MusicAlbumDto> findAllAlbumsByName(String userId, String name);

  Flux<MusicAlbumDto> findAllAlbums(Pageable pageable);

  Flux<MusicAlbumDto>  findUserLikedAlbumsByDate(String userId, Pageable pageable);

  Flux<MusicAlbumDto> findUserLikedAlbumsByTitle(String title, String userId, Pageable pageable);

  Mono<Long> countUserLikedAlbums(String userId);

  Mono<Long> countUserLikedAlbumsByTitle(String title, String userId);
}
