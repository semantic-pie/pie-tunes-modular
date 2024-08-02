package io.github.semanticpie.pietunes.metadata.core.services;

import io.github.semanticpie.pietunes.metadata.api.AlbumApi;
import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.MusicAlbumDto;
import io.github.semanticpie.pietunes.metadata.core.models.mappers.DomainEntityMapper;
import io.github.semanticpie.pietunes.metadata.core.repositories.MusicAlbumRepository;
import io.github.semanticpie.pietunes.metadata.core.repositories.globalSearch.AlbumSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AlbumDataService implements AlbumApi {

  private final MusicAlbumRepository albumRepository;
  private final AlbumSearchRepository albumSearchRepository;

  private final DomainEntityMapper entityMapper;

  @Override
  public Mono<MusicAlbumDto> findAlbumByUuid(String uuid) {
    return albumRepository.findMusicAlbumByUuid(uuid)
        .map(entityMapper::outerAlbum);
  }

  @Override
  public Flux<MusicAlbumDto> findAllAlbumsByName(String userId, String name) {
    return albumSearchRepository.findAllByName(userId, name);
  }

  @Override
  public Flux<MusicAlbumDto> findAllAlbums(Pageable pageable) {
    return albumRepository.findAllAlbums(pageable)
        .map(entityMapper::outerAlbumWithoutTracks);
  }

  @Override
  public Flux<MusicAlbumDto> findUserLikedAlbumsByDate(String userId, Pageable pageable) {
    return albumRepository.findAllLikedAlbums(userId,
        pageable).map(foundAlbum -> {
      MusicAlbumDto albumDto = entityMapper.outerAlbumWithoutTracks(foundAlbum);
      albumDto.setIsLiked(true);
      return albumDto;
    });
  }

  @Override
  public Flux<MusicAlbumDto> findUserLikedAlbumsByTitle(String title, String userId,
      Pageable pageable) {
    return albumRepository.findAllLikedAlbumsByTitle(title, userId,
        pageable).map(foundAlbum -> {
      MusicAlbumDto albumDto = entityMapper.outerAlbumWithoutTracks(foundAlbum);
      albumDto.setIsLiked(true);
      return albumDto;
    });
  }

  @Override
  public Mono<Long> countUserLikedAlbums(String userId) {
    return albumRepository.findTotalLikedAlbums(userId);
  }

  @Override
  public Mono<Long> countUserLikedAlbumsByTitle(String title, String userId) {
    return albumRepository.findTotalLikedAlbumsByTitle(title, userId);
  }
}
