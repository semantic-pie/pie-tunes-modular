package io.github.semanticpie.pietunes.metadata.core.services;

import io.github.semanticpie.pietunes.metadata.api.TrackApi;
import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.MusicTrackDto;
import io.github.semanticpie.pietunes.metadata.core.models.MusicAlbum;
import io.github.semanticpie.pietunes.metadata.core.models.MusicBand;
import io.github.semanticpie.pietunes.metadata.core.models.MusicGenre;
import io.github.semanticpie.pietunes.metadata.core.models.MusicTrack;
import io.github.semanticpie.pietunes.metadata.core.models.mappers.DomainEntityMapper;
import io.github.semanticpie.pietunes.metadata.core.repositories.MusicAlbumRepository;
import io.github.semanticpie.pietunes.metadata.core.repositories.MusicBandRepository;
import io.github.semanticpie.pietunes.metadata.core.repositories.MusicGenreRepository;
import io.github.semanticpie.pietunes.metadata.core.repositories.MusicTrackRepository;
import io.github.semanticpie.pietunes.metadata.core.repositories.globalSearch.TrackSearchRepository;

import java.util.HashSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackDataService implements TrackApi {

  private final MusicTrackRepository musicTrackRepository;
  private final MusicAlbumRepository musicAlbumRepository;
  private final MusicBandRepository musicBandRepository;
  private final MusicGenreRepository musicGenreRepository;
  private final TrackSearchRepository trackSearchRepository;
  private final DomainEntityMapper entityMapper;


  @Override
  public Mono<MusicTrackDto> findByNameAndBandName(String name, String bandName) {
    return musicTrackRepository.findByTitleAndMusicBand_Name(name, bandName)
        .map(entityMapper::outerTrack);
  }

  @Override
  public Flux<MusicTrackDto> findAllTracks(Pageable pageable) {
    return musicTrackRepository.findAllTracks(pageable)
        .map(entityMapper::outerTrack);
  }

  @Override
  public Flux<MusicTrackDto> findAllTracksByName(String userId, String name) {
    return trackSearchRepository.findAllByName(userId, name);
  }

  @Override
  public Flux<MusicTrackDto> findAllLikedTracks(String userId, Pageable pageable) {
    return musicTrackRepository.findAllLikedTracks(userId,
        pageable).map(foundTrack -> {
      MusicTrackDto trackDto = entityMapper.outerTrack(foundTrack);
      trackDto.setIsLiked(true);
      return trackDto;
    });
  }

  @Override
  public Mono<MusicTrackDto> findMusicTrackById(String id) {
    return musicTrackRepository.findMusicTrackByUuid(id)
        .map(entityMapper::outerTrack);
  }

  @Override
  public Mono<Long> findTotalLikedTracks(String userId) {
    return musicTrackRepository.findTotalLikedTracks(userId);
  }

  @Override
  public Mono<Long> findTotalLikedTracksByTitle(String title, String userId) {
    return musicTrackRepository.findTotalLikedTracksByTitle(title, userId);
  }

  @Override
  public Flux<MusicTrackDto> findAllLikedTracksByTitle(String title, String userId,
      Pageable pageable) {
    return musicTrackRepository.findAllLikedTracksByTitle(title, userId,
        pageable).map(foundTrack -> {
      MusicTrackDto trackDto = entityMapper.outerTrack(foundTrack);
      trackDto.setIsLiked(true);
      return trackDto;
    });
  }

  @Override
  public Mono<Long> findTotalTracksInAlbumByUuid(String albumId) {
    return musicTrackRepository.findTotalTracksInAlbumByUuid(albumId);
  }

  @Override
  public Flux<MusicTrackDto> findTracksByAlbumUuid(String albumId) {
    return musicTrackRepository.findTracksByAlbumUuid(albumId).map(foundTrack -> {
      MusicTrackDto trackDto = entityMapper.outerTrack(foundTrack);
      trackDto.setIsLiked(true);
      return trackDto;
    });
  }

  @Override
  public Mono<MusicTrackDto> saveTrack(MusicTrackDto musicTrackDto) {
    log.info("Save track: {} - {}", musicTrackDto.getTitle(), musicTrackDto.getMusicBand().name());

    var musicBandDto = musicTrackDto.getMusicBand();
    MusicBand musicBand = new MusicBand(musicBandDto.uuid(), musicBandDto.name(),
        musicBandDto.description());
    var musicAlbumDto = musicTrackDto.getMusicAlbum();
    MusicAlbum musicAlbum = new MusicAlbum(musicAlbumDto.uuid(), musicAlbumDto.name(),
        musicAlbumDto.description(), musicAlbumDto.yearOfRecord());

    Mono<MusicBand> bandMono = musicBandRepository
        .findMusicBandByName(musicBand.getName())
        .switchIfEmpty(Mono.defer(() ->
            musicBandRepository.save(musicBand)));

    Mono<MusicAlbum> albumMono = musicAlbumRepository
        .findMusicAlbumByName(musicAlbum.getName())
        .switchIfEmpty(Mono.defer(() -> musicAlbumRepository.save(musicAlbum)));

    Flux<MusicGenre> genreFlux = Flux.fromIterable(musicTrackDto.getGenres())
        .flatMap(g -> musicGenreRepository.findMusicGenreByName(g)
            .switchIfEmpty(Mono.defer(() -> musicGenreRepository.save(new MusicGenre(g)))));

    return Mono.zip(bandMono, albumMono, genreFlux.collectList())
        .flatMap(tuple -> {
          var band = tuple.getT1();
          var album = tuple.getT2();

          var musicTrack = new MusicTrack(musicTrackDto.getUuid(), musicTrackDto.getTitle(),
              musicTrackDto.getReleaseYear(), musicTrackDto.getBitrate(),
              musicTrackDto.getLengthInMilliseconds());

          musicTrack.setMusicBand(band);

          musicTrack.setMusicAlbum(album);

          album.setMusicBand(band);

          musicTrack.setGenres(new HashSet<>(tuple.getT3()));

          return musicTrackRepository.save(musicTrack).map(entityMapper::outerTrack);
        });
  }
}
