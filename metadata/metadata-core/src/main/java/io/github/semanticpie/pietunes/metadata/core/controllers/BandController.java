package io.github.semanticpie.pietunes.metadata.core.controllers;

import io.github.semanticpie.pietunes.metadata.core.models.MusicBand;
import io.github.semanticpie.pietunes.metadata.core.models.dtos.domain.MusicBandDto;
import io.github.semanticpie.pietunes.metadata.core.models.mappers.DomainEntityMapper;
import io.github.semanticpie.pietunes.metadata.core.repositories.MusicBandRepository;
import io.github.semanticpie.pietunes.metadata.core.services.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



@Slf4j
@RestController
@CrossOrigin(origins = "*")
@AllArgsConstructor
@RequestMapping("/api/v1/library/artists")
public class BandController {

    private final MusicBandRepository musicBandRepository;
    private final DomainEntityMapper entityMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @Deprecated
    @GetMapping()
    public Flux<MusicBandDto> getMethodName(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "8") int limit) {

        Pageable pageable = PageRequest.of(page, limit);
        return musicBandRepository.findAllBands(pageable)
                .map(entityMapper::outerBandWithoutAlbums);
    }


    @GetMapping("/{uuid}")
    @Parameter(in = ParameterIn.PATH, name = "uuid", description = "Band uuid")
    public ResponseEntity<Mono<MusicBandDto>>
    findTrackByUuid(@PathVariable String uuid) {
        return ResponseEntity.ok()
                .body(musicBandRepository.findMusicBandByUuid(uuid)
                        .map(entityMapper::outerBand));
    }

    @GetMapping("/find-by-date")
    @Parameter(in = ParameterIn.QUERY, name = "order", schema = @Schema(type = "string", allowableValues = {"asc", "desc"}))
    @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "integer", minimum = "0"))
    @Parameter(in = ParameterIn.QUERY, name = "limit", schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
    @Parameter(in = ParameterIn.QUERY, name = "userUuid", schema = @Schema(type = "string"))
    // This user-cringe uuid parameter will be deleted after security implementation
    public ResponseEntity<Flux<MusicBandDto>>
    findArtistsByDate(@RequestParam(defaultValue = "0") int page,
                      @RequestParam(defaultValue = "16") int limit,
                      @RequestParam(defaultValue = "desc") String order,
                      ServerWebExchange exchange) {

        String jwtToken = jwtTokenProvider.getJwtTokenFromRequest(exchange.getRequest());
        String userUuid = jwtTokenProvider.getUUID(jwtToken);

        Sort sort = Sort.by(Sort.Direction.fromString(order.toLowerCase()), "r.createdAt");
        Pageable pageable = PageRequest.of(page, limit, sort);

        Mono<Long> totalLikedBands =
                musicBandRepository.findTotalLikedBands(userUuid);

        Flux<MusicBand> allLikedBands =
                musicBandRepository.findAllLikedBands(userUuid, pageable);

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(totalLikedBands.block()))
                .body(allLikedBands.map(foundBand -> {
                    MusicBandDto bandDto = entityMapper.outerBandWithoutAlbums(foundBand);
                    bandDto.setIsLiked(true);
                    return bandDto;
                }));
    }

    @GetMapping("/find-by-title")
    @Parameter(in = ParameterIn.QUERY, name = "q", schema = @Schema(type = "string", minLength = 1, maxLength = 20))
    @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "integer", minimum = "0"))
    @Parameter(in = ParameterIn.QUERY, name = "limit", schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
    @Parameter(in = ParameterIn.QUERY, name = "userUuid", schema = @Schema(type = "string"))
    // This user-cringe uuid parameter will be deleted after security implementation
    public ResponseEntity<Flux<MusicBandDto>>
    findByTitle(@RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "16") int limit,
                @RequestParam(value = "q") String query,
                ServerWebExchange exchange) {

        String jwtToken = jwtTokenProvider.getJwtTokenFromRequest(exchange.getRequest());
        String userUuid = jwtTokenProvider.getUUID(jwtToken);

        Pageable pageable = PageRequest.of(page, limit);

        Mono<Long> totalLikedBandsByTitle =
                musicBandRepository.findTotalLikedBandsByTitle(query.toLowerCase(), userUuid);

        Flux<MusicBand> allLikedBandsByTitle =
                musicBandRepository.findAllLikedBandsByTitle(query.toLowerCase(), userUuid, pageable);

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(totalLikedBandsByTitle.block()))
                .body(allLikedBandsByTitle.map(foundBand -> {
                    MusicBandDto bandDto = entityMapper.outerBandWithoutAlbums(foundBand);
                    bandDto.setIsLiked(true);
                    return bandDto;
                }));
    }

}
