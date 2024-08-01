package io.github.semanticpie.pietunes.metadata.core.services.impl;

import io.github.semanticpie.pietunes.metadata.core.models.MusicAlbum;
import io.github.semanticpie.pietunes.metadata.core.models.MusicBand;
import io.github.semanticpie.pietunes.metadata.core.models.MusicGenre;
import io.github.semanticpie.pietunes.metadata.core.models.MusicTrack;
import io.github.semanticpie.pietunes.metadata.core.repositories.MusicAlbumRepository;
import io.github.semanticpie.pietunes.metadata.core.repositories.MusicBandRepository;
import io.github.semanticpie.pietunes.metadata.core.repositories.MusicGenreRepository;
import io.github.semanticpie.pietunes.metadata.core.repositories.MusicTrackRepository;
import io.github.semanticpie.pietunes.metadata.core.services.TrackLoaderService;
import io.github.semanticpie.pietunes.metadata.core.services.exceptions.NodeAlreadyExists;
import io.github.semanticpie.pietunes.metadata.core.utils.TrackMetadataParser;
import io.github.semanticpie.pietunes.shared.spi.adapter.minio.config.MinioConfiguration;
import io.minio.GenericResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackLoaderServiceImpl implements TrackLoaderService {

    @Autowired
    TrackLoaderServiceImpl self;

    private final MinioConfiguration minioConfiguration;

    private final MinioClient minioClient;
    private final MusicTrackRepository musicTrackRepository;
    private final MusicBandRepository musicBandRepository;
    private final MusicGenreRepository musicGenreRepository;
    private final MusicAlbumRepository musicAlbumRepository;

    private final TrackMetadataParser parser;

    @Override
    public Mono<Void> saveAll(List<FilePart> trackFiles) {
        Queue<FilePart> tracksQueue = new LinkedList<>(trackFiles);
        return recursiveSave(self.save(tracksQueue.remove()), tracksQueue).then(Mono.empty());
    }

    private Mono<MusicTrack> recursiveSave(Mono<MusicTrack> track, Queue<FilePart> files) {
        if (!files.isEmpty())
            return track.flatMap(t -> recursiveSave(self.save(files.remove()), files));
        else
            return track;
    }

    @Override
    @Transactional
    public Mono<MusicTrack> save(FilePart filePart) {
        try {
            var result = parser.parse(filePart);
            MusicTrack musicTrack = result.getMusicTrack();
            musicTrack.setReleaseYear("1988");

            return musicTrackRepository
                    .findByTitleAndMusicBand_Name(
                            musicTrack.getTitle(),
                            musicTrack.getMusicBand().getName())
                    .flatMap((existingTrack) -> {
                        String errorMessage = String.format("Track with name '%s' and artist '%s' already exists.",
                                existingTrack.getTitle(), existingTrack.getMusicBand().getName());
                        return Mono.error(new NodeAlreadyExists(errorMessage));
                    })
                    .switchIfEmpty(
                            saveNeo4j(musicTrack)
                                    .flatMap(persistedTrack -> saveMinio(persistedTrack, filePart, result.getCover(),
                                            result.getCoverMimeType())))
                    .cast(MusicTrack.class);
        } catch (RuntimeException exception) {
            return Mono.error(exception);
        }
    }

    protected Mono<MusicTrack> saveNeo4j(MusicTrack musicTrack) {
        log.info("Save track: {} - {}", musicTrack.getTitle(), musicTrack.getMusicBand().getName());

        MusicBand musicBand = musicTrack.getMusicBand();
        MusicAlbum musicAlbum = musicTrack.getMusicAlbum();

        Set<MusicGenre> musicGenres = musicTrack.getGenres();

        Mono<MusicBand> bandMono = musicBandRepository
                .findMusicBandByName(musicBand.getName())
                .switchIfEmpty(Mono.defer(() -> 
                    musicBandRepository.save(musicBand)));

        Mono<MusicAlbum> albumMono = musicAlbumRepository
                .findMusicAlbumByName(musicAlbum.getName())
                .switchIfEmpty(Mono.defer(() -> musicAlbumRepository.save(musicAlbum)));

        Flux<MusicGenre> genreFlux = Flux.fromIterable(musicGenres)
                .flatMap(g -> musicGenreRepository.findMusicGenreByName(g.getName())
                        .switchIfEmpty(Mono.defer(() -> musicGenreRepository.save(g))));

        return Mono.zip(bandMono, albumMono, genreFlux.collectList())
                .flatMap(tuple -> {
                    var band = tuple.getT1();
                    var album = tuple.getT2();

                    musicTrack.setMusicBand(band);
                    musicTrack.setMusicAlbum(album);

                    album.setMusicBand(band);

                    musicTrack.setGenres(new HashSet<>(tuple.getT3()));

                    return musicTrackRepository.save(musicTrack);
                });
    }

    private Mono<MusicTrack> saveMinio(MusicTrack musicTrack, FilePart file, byte[] cover, String coverContentType) {
        String trackContentType = Objects.requireNonNull(file.headers().getContentType()).toString();
        String trackObjectName = musicTrack.getUuid().toString();
        String coverObjectName = musicTrack.getMusicAlbum().getUuid().toString();

        return DataBufferUtils.join(file.content())
                .flatMap(dataBuffer -> {
                    log.info("Save track to MinIO '{}' : '{}'", musicTrack.getTitle(), trackObjectName);

                    try (
                            InputStream trackInputStream = dataBuffer.asInputStream();
                            InputStream coverInputStream = new ByteArrayInputStream(cover)) {
                        return Mono.zip(
                                // save track data
                                Mono.just(minioClient.putObject(
                                        PutObjectArgs.builder()
                                                .bucket(minioConfiguration.trackBucketName)
                                                .object(trackObjectName)
                                                .contentType(trackContentType)
                                                .stream(trackInputStream, trackInputStream.available(), -1)
                                                .build())),
                                // save track cover if not exist
                                isCoverExist(coverObjectName).switchIfEmpty(Mono.defer(() -> {
                                            try {
                                                return Mono.just(minioClient.putObject(
                                                        PutObjectArgs.builder()
                                                                .bucket(
                                                                    minioConfiguration.coverBucketName)
                                                                .object(coverObjectName)
                                                                .contentType(coverContentType)
                                                                .stream(coverInputStream, coverInputStream.available(), -1)
                                                                .build()));
                                            } catch (Exception e) {
                                                throw new RuntimeException(e);
                                            }
                                        })))
                                .flatMap((ignore) -> Mono.just(musicTrack));
                    } catch (Exception ex) {
                        return Mono.empty();
                    }
                });
    }

    public Mono<GenericResponse> isCoverExist(String name) {
        try {
            return Mono.just(minioClient.statObject(StatObjectArgs.builder()
                    .bucket(minioConfiguration.coverBucketName)
                    .object(name).build()));
        } catch (ErrorResponseException e) {
            return Mono.empty();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void logAllAboutTrack(MusicTrack musicTrack) {
        log.info("title: {}", musicTrack.getTitle());
        log.info("releaseYear: {}", musicTrack.getReleaseYear());
        log.info("bitrate: {}", musicTrack.getBitrate());
        log.info("lengthInMilliseconds: {}", musicTrack.getLengthInMilliseconds());
        log.info("version: {}", musicTrack.getVersion());
        log.info("uuid: {}", musicTrack.getUuid());
        log.info("\n---- genres ----");
        for (MusicGenre musicGenre : musicTrack.getGenres()) {
            log.info("name: {}", musicGenre.getName());
            log.info("version: {}\n", musicGenre.getVersion());
        }

        log.info("---- band ----");
        var band = musicTrack.getMusicBand();
        log.info("name: {}", band.getName());
        log.info("description: {}", band.getDescription());
        log.info("version: {}", band.getVersion());
        log.info("uuid: {}\n", band.getUuid());

        log.info("---- album ----");
        var album = musicTrack.getMusicAlbum();
        log.info("name: {}", album.getName());
        log.info("description: {}", album.getDescription());
        log.info("year: {}", album.getYearOfRecord());
        log.info("version: {}", album.getVersion());
        log.info("uuid: {}\n", album.getUuid());

        log.info("genres: {}", musicTrack.getGenres().stream().map(MusicGenre::getName).toList());
        log.info(": {}",
        musicTrack.getGenres().stream().map(MusicGenre::getName).toList());
    }
}