package io.github.semanticpie.pietunes.metadata.core.services;

import io.github.semanticpie.pietunes.metadata.api.BandApi;
import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.MusicBandDto;
import io.github.semanticpie.pietunes.metadata.core.models.mappers.DomainEntityMapper;
import io.github.semanticpie.pietunes.metadata.core.repositories.MusicBandRepository;
import io.github.semanticpie.pietunes.metadata.core.repositories.globalSearch.BandSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BandDataService implements BandApi {

  private final MusicBandRepository musicBandRepository;
  private final BandSearchRepository bandSearchRepository;
  private final DomainEntityMapper entityMapper;

  @Override
  public Flux<MusicBandDto> findAllBandsByName(String userId, String name) {
    return bandSearchRepository.findAllByName(userId, name);
  }

  @Override
  public Flux<MusicBandDto> getAllBands(Pageable pageable) {
    return musicBandRepository.findAllBands(pageable)
        .map(entityMapper::outerBandWithoutAlbums);
  }

  @Override
  public Mono<MusicBandDto> getBandById(String bandId) {
    return musicBandRepository.findMusicBandByUuid(bandId)
        .map(entityMapper::outerBand);
  }

  @Override
  public Flux<MusicBandDto> getAllUserLikedBands(String userId, Pageable pageable) {
    return musicBandRepository.findAllLikedBands(userId,
        pageable).map(foundBand -> {
      MusicBandDto bandDto = entityMapper.outerBandWithoutAlbums(foundBand);
      bandDto.setIsLiked(true);
      return bandDto;
    });
  }

  @Override
  public Flux<MusicBandDto> getAllUserLikedBandsByTitle(String title, String userId,
      Pageable pageable) {
    return musicBandRepository.findAllLikedBandsByTitle(title, userId,
        pageable).map(foundBand -> {
      MusicBandDto bandDto = entityMapper.outerBandWithoutAlbums(foundBand);
      bandDto.setIsLiked(true);
      return bandDto;
    });
  }

  @Override
  public Mono<Long> countUserLikedBands(String userId) {
    return musicBandRepository.findTotalLikedBands(userId);
  }

  @Override
  public Mono<Long> countUserLikedBandsByTitle(String userId, String title) {
    return musicBandRepository.findTotalLikedBandsByTitle(title, userId);
  }
}
