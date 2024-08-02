package io.github.semanticpie.pietunes.metadata.api.adapter.spring.webflux.controllers;

import io.github.semanticpie.pietunes.jwt.token.provider.JwtTokenProvider;
import io.github.semanticpie.pietunes.metadata.api.TrackApi;
import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.MusicTrackDto;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/library/tracks")
public class TrackController {

  private final TrackApi trackApi;
  private final JwtTokenProvider jwtTokenProvider;

  @Deprecated
  @GetMapping()
  public Flux<MusicTrackDto> getMethodName(@RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "8") int limit) {

    Pageable pageable = PageRequest.of(page, limit);
    return trackApi.findAllTracks(pageable);
  }

  @GetMapping("/{uuid}")
  @Parameter(in = ParameterIn.PATH, name = "uuid", description = "Track uuid")
  public ResponseEntity<Mono<MusicTrackDto>>
  findTrackByUuid(@PathVariable String uuid) {
    return ResponseEntity.ok()
        .body(trackApi.findMusicTrackById(uuid));
  }

  @GetMapping("/find-by-date")
  @Parameter(in = ParameterIn.QUERY, name = "order", schema = @Schema(type = "string", allowableValues = {
      "asc", "desc"}))
  @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "integer", minimum = "0"))
  @Parameter(in = ParameterIn.QUERY, name = "limit", schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
  @Parameter(in = ParameterIn.QUERY, name = "userUuid", schema = @Schema(type = "string"))
// This user-cringe uuid parameter will be deleted after security implementation
  public ResponseEntity<Flux<MusicTrackDto>>
  findTracksByDate(@RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "16") int limit,
      @RequestParam(defaultValue = "desc") String order,
      ServerWebExchange exchange) {
    String jwtToken = jwtTokenProvider.getJwtTokenFromRequest(exchange.getRequest());
    String userId = jwtTokenProvider.getUUID(jwtToken);

    Sort sort = Sort.by(Sort.Direction.fromString(order.toLowerCase()), "r.createdAt");
    Pageable pageable = PageRequest.of(page, limit, sort);

    Mono<Long> totalLikedTracks = trackApi.findTotalLikedTracks(userId);

    Flux<MusicTrackDto> allLikedTracks = trackApi.findAllLikedTracks(userId,
        pageable);

    return ResponseEntity.ok()
        .header("X-Total-Count", String.valueOf(totalLikedTracks.block()))
        .body(allLikedTracks);
  }

  @GetMapping("/find-by-title")
  @Parameter(in = ParameterIn.QUERY, name = "q", schema = @Schema(type = "string", minLength = 1, maxLength = 20))
  @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "integer", minimum = "0"))
  @Parameter(in = ParameterIn.QUERY, name = "limit", schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
  @Parameter(in = ParameterIn.QUERY, name = "userUuid", schema = @Schema(type = "string"))
// This user-cringe uuid parameter will be deleted after security implementation
  public ResponseEntity<Flux<MusicTrackDto>>
  findTracksByTitle(@RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "16") int limit,
      @RequestParam(value = "q") String query,
      ServerWebExchange exchange) {
    String jwtToken = jwtTokenProvider.getJwtTokenFromRequest(exchange.getRequest());
    String userId = jwtTokenProvider.getUUID(jwtToken);

    Pageable pageable = PageRequest.of(page, limit);

    Mono<Long> totalLikedTracksByTitle = trackApi.findTotalLikedTracksByTitle(query.toLowerCase(),
        userId);

    Flux<MusicTrackDto> allLikedTracksByTitle = trackApi.findAllLikedTracksByTitle(
        query.toLowerCase(), userId, pageable);

    return ResponseEntity.ok()
        .header("X-Total-Count", String.valueOf(totalLikedTracksByTitle.block()))
        .body(allLikedTracksByTitle);
  }

  @GetMapping("/find-by-album/{uuid}")
  @Operation(description = "Find all tracks of album with uuid.")
  @Parameter(in = ParameterIn.PATH, name = "uuid", description = "Album uuid")
  public ResponseEntity<Flux<MusicTrackDto>>
  findTracksByAlbum(@PathVariable String uuid) {

    Mono<Long> totalTracksInAlbum = trackApi.findTotalTracksInAlbumByUuid(uuid);

    Flux<MusicTrackDto> allTracksInAlbum = trackApi.findTracksByAlbumUuid(uuid);

    return ResponseEntity.ok()
        .header("X-Total-Count", String.valueOf(totalTracksInAlbum.block()))
        .body(allTracksInAlbum);
  }
}
