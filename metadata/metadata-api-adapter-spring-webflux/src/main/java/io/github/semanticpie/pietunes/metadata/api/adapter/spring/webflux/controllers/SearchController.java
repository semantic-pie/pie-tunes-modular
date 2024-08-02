package io.github.semanticpie.pietunes.metadata.api.adapter.spring.webflux.controllers;

import io.github.semanticpie.pietunes.jwt.token.provider.JwtTokenProvider;
import io.github.semanticpie.pietunes.metadata.api.AlbumApi;
import io.github.semanticpie.pietunes.metadata.api.BandApi;
import io.github.semanticpie.pietunes.metadata.api.TrackApi;
import io.github.semanticpie.pietunes.metadata.api.adapter.spring.webflux.models.SearchEntityResponse;
import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.MusicAlbumDto;
import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.MusicBandDto;
import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.MusicTrackDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@Slf4j
@CrossOrigin(origins = "*")
@AllArgsConstructor
@RequestMapping(path = "/api/v1")
public class SearchController {

  private final TrackApi trackApi;
  private final AlbumApi albumApi;
  private final BandApi bandApi;
  private final JwtTokenProvider jwtTokenProvider;

  @GetMapping("/search")
  @Parameter(in = ParameterIn.QUERY, name = "q", schema = @Schema(type = "string", minLength = 1, maxLength = 20))
  @Parameter(in = ParameterIn.QUERY, name = "userUuid", schema = @Schema(type = "string"))
  public Mono<SearchEntityResponse>
  globalSearchItems(@RequestParam(value = "q") String searchQuery, ServerWebExchange exchange) {

    String jwtToken = jwtTokenProvider.getJwtTokenFromRequest(exchange.getRequest());
    String userId = jwtTokenProvider.getUUID(jwtToken);

    Flux<MusicTrackDto> tracks = trackApi.findAllTracksByName(userId,
        searchQuery.toLowerCase()).take(4);
    Flux<MusicAlbumDto> albums = albumApi.findAllAlbumsByName(userId,
        searchQuery.toLowerCase());
    Flux<MusicBandDto> bands = bandApi.findAllBandsByName(userId,
        searchQuery.toLowerCase());

    return Mono.zip(tracks.collectList(), albums.collectList(), bands.collectList())
        .map(tuple -> {
          SearchEntityResponse searchEntityResponse = new SearchEntityResponse();
          searchEntityResponse.setTracks(tuple.getT1());
          searchEntityResponse.setAlbums(tuple.getT2());
          searchEntityResponse.setBands(tuple.getT3());
          return searchEntityResponse;
        });
  }

}
