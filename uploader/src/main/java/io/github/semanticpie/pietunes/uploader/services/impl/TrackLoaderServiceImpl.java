package io.github.semanticpie.pietunes.uploader.services.impl;

import io.github.semanticpie.pietunes.exceptions.NodeAlreadyExists;
import io.github.semanticpie.pietunes.metadata.api.TrackApi;
import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.MusicTrackDto;
import io.github.semanticpie.pietunes.shared.spi.adapter.minio.config.MinioConfiguration;
import io.github.semanticpie.pietunes.uploader.services.TrackLoaderService;
import io.github.semanticpie.pietunes.uploader.utils.TrackMetadataParser;
import io.minio.GenericResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackLoaderServiceImpl implements TrackLoaderService {

    @Autowired
    TrackLoaderServiceImpl self;

    private final MinioConfiguration minioConfiguration;

    private final MinioClient minioClient;
    private final TrackApi trackApi;

    private final TrackMetadataParser parser;

    @Override
    public Mono<Void> saveAll(List<FilePart> trackFiles) {
        Queue<FilePart> tracksQueue = new LinkedList<>(trackFiles);
        return recursiveSave(self.save(tracksQueue.remove()), tracksQueue).then(Mono.empty());
    }

    private Mono<MusicTrackDto> recursiveSave(Mono<MusicTrackDto> track, Queue<FilePart> files) {
        if (!files.isEmpty())
            return track.flatMap(t -> recursiveSave(self.save(files.remove()), files));
        else
            return track;
    }

    @Override
    public Mono<MusicTrackDto> save(FilePart filePart) {
        try {
            var result = parser.parse(filePart);
            MusicTrackDto musicTrack = result.getMusicTrack();
            musicTrack.setReleaseYear("1988");

            return trackApi.findByNameAndBandName(musicTrack.getTitle(), musicTrack.getMusicBand().name())
                    .flatMap((existingTrack) -> {
                        String errorMessage = String.format("Track with name '%s' and artist '%s' already exists.",
                                existingTrack.getTitle(), existingTrack.getMusicBand().name());
                        return Mono.error(new NodeAlreadyExists(errorMessage));
                    })
                    .switchIfEmpty(
                            trackApi.saveTrack(musicTrack)
                                    .flatMap(persistedTrack -> saveMinio(persistedTrack, filePart, result.getCover(),
                                            result.getCoverMimeType())))
                    .cast(MusicTrackDto.class);
        } catch (RuntimeException exception) {
            return Mono.error(exception);
        }
    }


    private Mono<MusicTrackDto> saveMinio(MusicTrackDto musicTrack, FilePart file, byte[] cover, String coverContentType) {
        String trackContentType = Objects.requireNonNull(file.headers().getContentType()).toString();
        String trackObjectName = musicTrack.getUuid().toString();
        String coverObjectName = musicTrack.getMusicAlbum().uuid().toString();

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

    private void logAllAboutTrack(MusicTrackDto musicTrack) {
        log.info("title: {}", musicTrack.getTitle());
        log.info("releaseYear: {}", musicTrack.getReleaseYear());
        log.info("bitrate: {}", musicTrack.getBitrate());
        log.info("lengthInMilliseconds: {}", musicTrack.getLengthInMilliseconds());
        log.info("uuid: {}", musicTrack.getUuid());
        log.info("\n---- genres ----");
        for (String musicGenre : musicTrack.getGenres()) {
            log.info("name: {}", musicGenre);
        }

        log.info("---- band ----");
        var band = musicTrack.getMusicBand();
        log.info("name: {}", band.name());
        log.info("description: {}", band.description());
        log.info("uuid: {}\n", band.uuid());

        log.info("---- album ----");
        var album = musicTrack.getMusicAlbum();
        log.info("name: {}", album.name());
        log.info("description: {}", album.description());
        log.info("year: {}", album.yearOfRecord());
        log.info("uuid: {}\n", album.uuid());
    }
}