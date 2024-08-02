package io.github.semanticpie.pietunes.metadata.core.controllers;

import io.github.semanticpie.pietunes.metadata.core.models.dtos.TrackLoaderResponseDto;
import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.MusicTrackDto;
import io.github.semanticpie.pietunes.metadata.core.models.mappers.DomainEntityMapper;
import io.github.semanticpie.pietunes.metadata.core.services.TrackLoaderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class TrackLoaderController {

    private final TrackLoaderService trackLoaderService;
    private final DomainEntityMapper domainEntityMapper;

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "Upload multiple multipart files. (overflow is possible)")
    @PostMapping(value = "/track-loader/upload", consumes = "multipart/form-data")
    public Mono<Void> handleFilesUpload(@RequestPart("file") Flux<FilePart> filePartFlux) {
        return filePartFlux.collectList()
                .flatMap(trackLoaderService::saveAll)
                .then();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "Upload single multipart file.")
    @PostMapping(value = "/track-loader/upload-one", consumes = "multipart/form-data")
    public Mono<TrackLoaderResponseDto> handleFileUpload(@RequestPart("file") Mono<FilePart> filePartFlux) {
        return filePartFlux
                .flatMap(trackLoaderService::save)
                .map(savedTrack -> {
                    MusicTrackDto trackDto = domainEntityMapper.outerTrack(savedTrack);
                    return new TrackLoaderResponseDto(trackDto);
                });
    }
}
